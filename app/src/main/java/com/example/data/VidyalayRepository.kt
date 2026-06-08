package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class VidyalayRepository(private val dao: VidyalayDao) {

    // ==========================================
    // Flow getters for Reactive UI Updates
    // ==========================================

    val subjects: Flow<List<SubjectEntity>> = dao.getAllSubjectsFlow()
    val badges: Flow<List<BadgeEntity>> = dao.getAllBadgesFlow()
    val profile: Flow<ProfileEntity?> = dao.getProfileFlow()
    val bookmarkedNotes: Flow<List<NoteEntity>> = dao.getBookmarkedNotesFlow()
    val quizAttempts: Flow<List<QuizAttemptEntity>> = dao.getAllQuizAttemptsFlow()
    val classes: Flow<List<ClassEntity>> = dao.getAllClassesFlow()

    fun getChaptersForSubjectFlow(subjectId: String): Flow<List<LessonChapterEntity>> {
        return dao.getChaptersForSubjectFlow(subjectId)
    }

    fun getNoteFlow(subjectId: String, chapterNumber: String): Flow<NoteEntity?> {
        return dao.getNoteFlow(subjectId, chapterNumber)
    }

    fun getQuizQuestionsFlow(subjectId: String, chapterNumber: String, difficulty: String): Flow<List<QuizQuestionEntity>> {
        return dao.getQuizQuestionsFlow(subjectId, chapterNumber, difficulty)
    }

    // ==========================================
    // Suspend operations for writes / edits
    // ==========================================

    suspend fun updateSubject(subject: SubjectEntity) {
        dao.updateSubject(subject)
    }

    suspend fun updateBadge(badge: BadgeEntity) {
        dao.updateBadge(badge)
    }

    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity) {
        dao.insertQuizAttempt(attempt)
    }

    suspend fun updateQuizAttempt(attempt: QuizAttemptEntity) {
        dao.updateQuizAttempt(attempt)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        dao.updateProfile(profile)
    }

    suspend fun insertNote(note: NoteEntity) {
        dao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        dao.updateNote(note)
    }

    suspend fun getUnsyncedQuizAttempts(): List<QuizAttemptEntity> {
        return dao.getUnsyncedQuizAttempts()
    }

    /**
     * Flags a chapter as completed in Room, automatically recalculates and caches subject progress,
     * updates the overall student profile course completion rate, and syncs progress to Google Firebase.
     */
    suspend fun markChapterCompleted(subjectId: String, chapterNumber: String) {
        try {
            val chaptersList = dao.getChaptersForSubjectFlow(subjectId).first()
            val targetChapter = chaptersList.find { it.chapterNumber == chapterNumber }
            if (targetChapter != null && targetChapter.status != "Completed") {
                val updatedChapter = targetChapter.copy(status = "Completed")
                dao.updateChapter(updatedChapter)
                
                // Recalculate subject progress
                val refreshChapters = dao.getChaptersForSubjectFlow(subjectId).first()
                val completedCount = refreshChapters.count { it.status == "Completed" }
                val totalChapters = refreshChapters.size
                if (totalChapters > 0) {
                    val progressPercent = (completedCount * 100) / totalChapters
                    // Retrieve original subject
                    val currentSubjects = dao.getAllSubjectsFlow().first()
                    currentSubjects.find { it.id == subjectId }?.let { originalSubject ->
                        dao.updateSubject(originalSubject.copy(progress = progressPercent))
                    }
                }
                
                // Recalculate overall class progress / courseCompletion in profile
                val refreshedSubjects = dao.getAllSubjectsFlow().first()
                if (refreshedSubjects.isNotEmpty()) {
                    val avgProgress = refreshedSubjects.map { it.progress }.average().toInt()
                    dao.getProfileFlow().first()?.let { currentProfile ->
                        val updatedProfile = currentProfile.copy(courseCompletion = avgProgress)
                        dao.updateProfile(updatedProfile)
                        
                        // Push to Cloud Firestore for dynamic progress monitoring
                        FirestoreManager.saveStudentProgress(
                            studentName = updatedProfile.studentName,
                            classLevel = updatedProfile.classSelected,
                            courseCompletion = avgProgress,
                            streakDays = updatedProfile.streakDays
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("VidyalayRepository", "Failed to mark chapter completed or recalculate progress metrics", e)
        }
    }

    // ==========================================
    // SYNC LOGIC: FIREBASE TO ROOM
    // ==========================================

    /**
     * Synergizes and downloads all available education material for a Class Level from Firebase Firestore.
     * Caches all assets to Room database to guarantee a complete offline learning experience.
     */
    suspend fun syncAllDataForClass(
        classLevel: Int,
        onProgress: (status: String, progress: Float) -> Unit
    ): Boolean {
        return try {
            Log.d("VidyalayRepository", "Starting multi-phase offline sync for Class $classLevel")
            
            // Phase 1: Sync Classes
            onProgress("Downloading available classes...", 0.1f)
            val baseClasses = listOf(
                ClassEntity(1, "Class 1", "child_care", "Early learning basics and phonics"),
                ClassEntity(2, "Class 2", "auto_stories", "Elementary reading and grammar"),
                ClassEntity(3, "Class 3", "science", "Core primary sciences and languages"),
                ClassEntity(4, "Class 4", "star", "Comprehensive standard primary curriculum"),
                ClassEntity(5, "Class 5", "architecture", "Advanced primary analysis and logic")
            )
            dao.insertClasses(baseClasses)

            // Phase 2: Sync Subjects
            onProgress("Fetching subjects for Class $classLevel...", 0.2f)
            val subjectsList = FirestoreManager.getSubjectsForClass(classLevel)
            if (subjectsList.isNotEmpty()) {
                dao.insertSubjects(subjectsList)
            }
            val activeSubjects = if (subjectsList.isNotEmpty()) subjectsList else dao.getAllSubjectsFlow().first()

            if (activeSubjects.isEmpty()) {
                Log.w("VidyalayRepository", "No subjects discovered. Sync terminated early.")
                onProgress("Sync complete (no subjects found)", 1.0f)
                return true
            }

            // Phase 3 & 4: Chapters, Notes & Quizzes
            val totalSteps = activeSubjects.size
            activeSubjects.forEachIndexed { sIndex, subject ->
                val baseProgress = 0.2f + (sIndex.toFloat() / totalSteps.toFloat()) * 0.7f
                onProgress("Downloading chapters for ${subject.title}...", baseProgress)
                
                // Fetch and store chapters
                val chapters = FirestoreManager.getChaptersForSubject(subject.id)
                if (chapters.isNotEmpty()) {
                    dao.insertChapters(chapters)
                }
                
                val sourceChapters = if (chapters.isNotEmpty()) chapters else getChaptersForSubjectFlow(subject.id).first()
                
                sourceChapters.forEachIndexed { cIndex, chapter ->
                    val chapProgName = "${subject.title} - Ch ${chapter.chapterNumber}"
                    onProgress("Syncing notes for $chapProgName...", baseProgress + (cIndex.toFloat() / sourceChapters.size.toFloat()) * 0.05f)
                    
                    // Fetch and store notes
                    val note = FirestoreManager.getNoteForChapter(subject.id, chapter.chapterNumber, chapter.chapterTitle)
                    dao.insertNote(note)

                    // Seed / Caching Quiz Questions offline
                    onProgress("Building quiz for $chapProgName...", baseProgress + (cIndex.toFloat() / sourceChapters.size.toFloat()) * 0.08f)
                    val difficulties = listOf("Easy", "Medium", "Hard")
                    for (diff in difficulties) {
                        val questions = QuizGenerator.generateQuizQuestions(subject.id, chapter.chapterNumber, diff)
                        val entities = questions.map { q ->
                            QuizQuestionEntity(
                                questionId = q.id,
                                subjectId = subject.id,
                                chapterNumber = chapter.chapterNumber,
                                difficulty = diff,
                                question = q.question,
                                formula = q.formula,
                                optionA = q.optionA,
                                optionB = q.optionB,
                                optionC = q.optionC,
                                optionD = q.optionD,
                                correctOption = q.correctOption
                            )
                        }
                        dao.insertQuizQuestions(entities)
                    }
                }
            }

            onProgress("All educational content synchronized offline!", 1.0f)
            Log.d("VidyalayRepository", "Successfully stored Classes, Subjects, Chapters, Notes, and Quizzes locally.")
            true
        } catch (e: Exception) {
            Log.e("VidyalayRepository", "Sync process crashed", e)
            onProgress("Sync failed: ${e.message}", 1.0f)
            false
        }
    }

    /**
     * Retrieves cached quiz questions from Room, automatically falling back of generating them in real time if unsynced.
     */
    suspend fun getOrGenerateQuizQuestions(
        subjectId: String,
        chapterNumber: String,
        difficulty: String
    ): List<QuizQuestion> {
        val cached = dao.getQuizQuestions(subjectId, chapterNumber, difficulty)
        return if (cached.isNotEmpty()) {
            cached.map { entity ->
                QuizQuestion(
                    id = entity.questionId,
                    question = entity.question,
                    formula = entity.formula,
                    optionA = entity.optionA,
                    optionB = entity.optionB,
                    optionC = entity.optionC,
                    optionD = entity.optionD,
                    correctOption = entity.correctOption
                )
            }
        } else {
            // Generate in real-time and cache immediately
            val list = QuizGenerator.generateQuizQuestions(subjectId, chapterNumber, difficulty)
            val entities = list.map { q ->
                QuizQuestionEntity(
                    questionId = q.id,
                    subjectId = subjectId,
                    chapterNumber = chapterNumber,
                    difficulty = difficulty,
                    question = q.question,
                    formula = q.formula,
                    optionA = q.optionA,
                    optionB = q.optionB,
                    optionC = q.optionC,
                    optionD = q.optionD,
                    correctOption = q.correctOption
                )
            }
            dao.insertQuizQuestions(entities)
            list
        }
    }
}
