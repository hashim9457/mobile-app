package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VidyalayDao {
    @Query("SELECT * FROM subjects")
    fun getAllSubjectsFlow(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Query("SELECT * FROM badges")
    fun getAllBadgesFlow(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY chapterNumber ASC")
    fun getChaptersForSubjectFlow(subjectId: String): Flow<List<LessonChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<LessonChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: LessonChapterEntity)

    @Query("SELECT * FROM profile WHERE id = 1")
    fun getProfileFlow(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    // NoteEntity Queries
    @Query("SELECT * FROM notes WHERE subjectId = :subjectId AND chapterNumber = :chapterNumber LIMIT 1")
    fun getNoteFlow(subjectId: String, chapterNumber: String): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE isBookmarked = 1")
    fun getBookmarkedNotesFlow(): Flow<List<NoteEntity>>

    // Quiz Attempts Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttemptsFlow(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE isSyncedToFirebase = 0")
    suspend fun getUnsyncedQuizAttempts(): List<QuizAttemptEntity>

    @Update
    suspend fun updateQuizAttempt(attempt: QuizAttemptEntity)

    // Classes Queries
    @Query("SELECT * FROM classes ORDER BY level ASC")
    fun getAllClassesFlow(): Flow<List<ClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassEntity>)

    // Quiz Questions Queries
    @Query("SELECT * FROM quiz_questions WHERE subjectId = :subjectId AND chapterNumber = :chapterNumber AND difficulty = :difficulty ORDER BY questionId ASC")
    fun getQuizQuestionsFlow(subjectId: String, chapterNumber: String, difficulty: String): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE subjectId = :subjectId AND chapterNumber = :chapterNumber AND difficulty = :difficulty ORDER BY questionId ASC")
    suspend fun getQuizQuestions(subjectId: String, chapterNumber: String, difficulty: String): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestionEntity>)
}
