package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Auth : Screen()
    object MainApp : Screen()
    object FirebaseDebug : Screen()
    data class SubjectDetail(val subjectId: String) : Screen()
    data class Quiz(val subjectId: String) : Screen()
    data class QuizResult(val score: Int, val total: Int, val timeMinutes: Int, val timeSeconds: Int) : Screen()
    data class ChapterNotes(val subjectId: String, val chapterNumber: String, val chapterTitle: String) : Screen()
}

enum class AppTab {
    Home, Lessons, Badges, Profile
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VidyalayDatabase.getInstance(application)
    private val dao = db.dao
    private val repository = VidyalayRepository(dao)

    // UI Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppTab.Home)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _selectedClass = MutableStateFlow(4) // Class 4 is default active
    val selectedClass: StateFlow<Int> = _selectedClass.asStateFlow()

    private val _selectedSubjectId = MutableStateFlow<String?>("mathematics")
    val selectedSubjectId: StateFlow<String?> = _selectedSubjectId.asStateFlow()

    // Offline Sync States
    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    private val _syncStatus = MutableStateFlow("")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _syncProgress = MutableStateFlow(0f)
    val syncProgress: StateFlow<Float> = _syncProgress.asStateFlow()

    // Room Database Flows wrapped in Repository Pattern
    val subjects = repository.subjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val badges = repository.badges.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val profile = repository.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val classes = repository.classes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chapters Flow linked to Selected Subject
    val selectedSubjectChapters = _selectedSubjectId.flatMapLatest { id ->
        if (id != null) repository.getChaptersForSubjectFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notes Caching State Manager
    private val _activeNotesParams = MutableStateFlow<Pair<String, String>?>(null) // (subjectId, chapterNumber)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeNote = _activeNotesParams.flatMapLatest { params ->
        if (params != null) {
            repository.getNoteFlow(params.first, params.second)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookmarkedNotes = repository.bookmarkedNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizAttempts = repository.quizAttempts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectChapterNotes(subjectId: String, chapterNumber: String, chapterTitle: String) {
        _activeNotesParams.value = Pair(subjectId, chapterNumber)
        navigateTo(Screen.ChapterNotes(subjectId, chapterNumber, chapterTitle))
        viewModelScope.launch {
            try {
                // Fetch from firestore (falls back to premium offline notes if cloud offline/unconfigured)
                val cloudNote = FirestoreManager.getNoteForChapter(subjectId, chapterNumber, chapterTitle)
                // Save locally to database cache for subsequent offline sessions
                repository.insertNote(cloudNote)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Note caching pipeline failed", e)
            }
        }
    }

    fun toggleNoteBookmark(note: NoteEntity) {
        viewModelScope.launch {
            val updated = note.copy(isBookmarked = !note.isBookmarked)
            repository.updateNote(updated)
        }
    }

    fun syncSubjectChapters(subjectId: String) {
        viewModelScope.launch {
            try {
                val cloudChapters = FirestoreManager.getChaptersForSubject(subjectId)
                if (cloudChapters.isNotEmpty()) {
                    dao.insertChapters(cloudChapters)
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error synchronizing subject chapters from cloud.", e)
            }
        }
    }

    // Quiz Questions & Active Quiz States
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _quizSubjectId = MutableStateFlow("mathematics")
    val quizSubjectId: StateFlow<String> = _quizSubjectId.asStateFlow()

    private val _quizChapterNumber = MutableStateFlow("01")
    val quizChapterNumber: StateFlow<String> = _quizChapterNumber.asStateFlow()

    private val _quizChapterTitle = MutableStateFlow("Introduction")
    val quizChapterTitle: StateFlow<String> = _quizChapterTitle.asStateFlow()

    private val _quizDifficulty = MutableStateFlow("Easy")
    val quizDifficulty: StateFlow<String> = _quizDifficulty.asStateFlow()

    private val _quizSecondsElapsed = MutableStateFlow(0)
    val quizSecondsElapsed: StateFlow<Int> = _quizSecondsElapsed.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val selectedAnswers: StateFlow<Map<Int, String>> = _selectedAnswers.asStateFlow()

    init {
        // Populate pre-fill standard data to satisfy real-world completeness standard and database room persistence rules
        viewModelScope.launch {
            repository.subjects.first().let { list ->
                if (list.isEmpty()) {
                    populateInitialData()
                }
            }
            
            // Reactively fetch subjects from Firestore (with Room fallback) whenever active class updates
            profile.filterNotNull().collectLatest { prof ->
                _selectedClass.value = prof.classSelected
                loadClassSubjects(prof.classSelected)
            }
        }
    }

    fun loadClassSubjects(classLevel: Int) {
        viewModelScope.launch {
            val fetchedSubjects = FirestoreManager.getSubjectsForClass(classLevel)
            if (fetchedSubjects.isNotEmpty()) {
                dao.insertSubjects(fetchedSubjects)
            }
        }
    }

    fun triggerFullOfflineSync() {
        viewModelScope.launch {
            _syncing.value = true
            val currentClass = _selectedClass.value
            repository.syncAllDataForClass(currentClass) { status, progress ->
                _syncStatus.value = status
                _syncProgress.value = progress
            }
            _syncing.value = false
        }
    }

    private suspend fun populateInitialData() {
        // 1. Subjects
        val initialSubjects = listOf(
            SubjectEntity(
                id = "english",
                category = "Language Arts",
                title = "English",
                progress = 65,
                isOfflineReady = true,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAi8Q1_Pk4gX-cF8CsS11GsI-6ZhaIGPFP6uwQUB30bajlL9pdndmVjeIpr3AB2GLVuCTNIXJhS2iQRZYxRYyvfofP9NFBYT3rXQQroaOgGAguXLVOLr6Y6XR5IZ0lldRMpfYdcKM0Jnv8V4IMQL1IbAhBBP2k0RCtNZg4t2HI1My4Edt1qyHB16X6g79cxfnZC1-iGa7dw5zN3mlT5e6oDx9T6wim55MY_ZWjDd3ZpdE2hbX4R_13XPc79bqaGybIn6L65iAdONuEy",
                iconName = "translate"
            ),
            SubjectEntity(
                id = "urdu",
                category = "National Language",
                title = "Urdu",
                progress = 40,
                isOfflineReady = false,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAks7WQLS74BXdYH32PGyHczpduRsoV817EOoYfZH-ODC25ZnrLE_fV3kDpFwF4GGWIEgHnWR8sLtkL93ToueYChfhZVQtYiL0q65UaucLKu3ifyFY-L-KBEcST34fTd2Tcjf478KEEmAs1kdYPA0lGe0pKXKuGQvID726-i9lpmz7f28uvOu8DxYWZ1iZhGuyrmu5IOBLlEgSEyIppvV8LL55BtxdkpUcoVeH6ySJXfpU0d2ZLhC1qWF6ShbEt8iF4UbmcQXak7TfE",
                iconName = "edit_note"
            ),
            SubjectEntity(
                id = "islamiyat",
                category = "Ethics & Values",
                title = "Islamiyat",
                progress = 85,
                isOfflineReady = true,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDpfrCllvC7Exn68taNbwb7EsfGBPo5nkN1ROmzOf_sin2bhurLGX9VtYBbYQEfEeYsJ7dCeQA6Cj25H-CnH2wt6weTnRux_eAotjKn21BeU699sWak2Y-PxtyVSowHspEmHva5hNayR44C7ckIIRnKC2SybkdD7uW-wM1BmnqNMSQr_bkvDYw4ng6m4eB0yhYKEionH62AYBDAxVVvw1OmDbG9nr4aO4GGr87d6iiQuuElRlr-HECXlSOvZlBizSnV1Xhd5Cda3qX9",
                iconName = "star"
            ),
            SubjectEntity(
                id = "general_knowledge",
                category = "Social Studies",
                title = "General Knowledge",
                progress = 20,
                isOfflineReady = false,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBjhRfl1Fu5bCQdhBN09ytqn3YgYQlZEQXAsoqmsfTA8kPVEWwr3_TbDS6PRNKKpWkrETFBiUOi2H2-FfuD_z5F09flwl9M4DHS1fXLLL4A72LrKiCYZv3NSuaOjr67XbrcCNzKt4H9Qw_LRKgfgAtWbWgil_rY63FYXSugvkGw6hP9F28G0-PsvGaPs6wq5qxfB6eauzOIubcFGKq9V-zAbDCxkLOebCsTHOb1EOiyoXP_SVH729bS9I3v8vFKcwdGRhBHQtY2oIBv",
                iconName = "public"
            ),
            SubjectEntity(
                id = "mathematics",
                category = "Logic & Science",
                title = "Mathematics",
                progress = 55,
                isOfflineReady = true,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnrNnn0k_dpmz5tMgN7_JLZijkja-XkVefhLLznFAFunKANfyguHJsAbw_3SIw0TFHtw2MFLENzeM4SgQyf_PbDBZo6EULoJKZj5-eGA72Tamka3tjgT45IK7BcL91mk0MJdC6BdgriEmeOpY3Wfu6ndpWTZZDoDwzUwTe2zbkgrIVWnNS4j0Py_o7NWnQCDr84CQKj3nJLca6pZJJd1s3GUGyZ5k1uz9mzvwS_cMR10xxbR86KWhKKx4rmsq1PCFxGQWyvCW9oS27",
                iconName = "calculate"
            )
        )
        dao.insertSubjects(initialSubjects)

        // 2. Badges
        val initialBadges = listOf(
            BadgeEntity(
                id = "district_vanguard",
                title = "District Vanguard",
                description = "Awarded for ranking in the top 1% of students in the Punjab region. You have demonstrated exceptional mastery across 12 diverse subjects.",
                tier = "District Champion",
                status = "UNLOCKED",
                unlockedAt = "Unlocked Oct 2023",
                studentsCount = "542 Students have this",
                iconName = "workspace_premium"
            ),
            BadgeEntity(
                id = "math_wizard",
                title = "Math Wizard",
                description = "Master intermediate algebraic variables and equation solutions to secure the prestigious Golden star badge.",
                tier = "GOLD TIER",
                status = "UNLOCKED",
                unlockedAt = "Unlocked Oct 2023",
                studentsCount = "1.2k Students have this",
                iconName = "star"
            ),
            BadgeEntity(
                id = "historian",
                title = "Historian",
                description = "Demonstrated deep historical knowledge in local curriculum chapters.",
                tier = "SILVER TIER",
                status = "UNLOCKED",
                unlockedAt = "Unlocked Nov 2023",
                studentsCount = "920 Students have this",
                iconName = "history_edu"
            ),
            BadgeEntity(
                id = "eco_warrior",
                title = "Eco Warrior",
                description = "Successfully solved all environmental science and logical eco-quizzes.",
                tier = "BRONZE TIER",
                status = "UNLOCKED",
                unlockedAt = "Unlocked Jan 2024",
                studentsCount = "3.2k Students have this",
                iconName = "eco"
            ),
            BadgeEntity(
                id = "code_ninja",
                title = "Code Ninja",
                description = "Awarded for coding concepts and solving visual instructions without compiler errors.",
                tier = "LOCKED",
                status = "LOCKED",
                unlockedAt = null,
                studentsCount = "Locked",
                iconName = "lock"
            ),
            BadgeEntity(
                id = "space_explorer",
                title = "Space Explorer",
                description = "Complete solar system and advanced physics modules to unlock this galaxy badge.",
                tier = "LOCKED",
                status = "LOCKED",
                unlockedAt = null,
                studentsCount = "Locked",
                iconName = "lock"
            ),
            BadgeEntity(
                id = "polyglot",
                title = "Polyglot",
                description = "Demonstrate multilingual mastery in English, Urdu, and Ethics vocabulary quizzes.",
                tier = "LOCKED",
                status = "LOCKED",
                unlockedAt = null,
                studentsCount = "Locked",
                iconName = "lock"
            )
        )
        dao.insertBadges(initialBadges)

        // 3. Chapters (Example: Mathematics)
        val initialChapters = listOf(
            LessonChapterEntity(subjectId = "mathematics", chapterNumber = "01", chapterTitle = "Real Numbers & Sets", status = "Completed", lessonsCount = 4),
            LessonChapterEntity(subjectId = "mathematics", chapterNumber = "02", chapterTitle = "Calculus: Limits & Continuity", status = "In Progress", lessonsCount = 8),
            LessonChapterEntity(subjectId = "mathematics", chapterNumber = "03", chapterTitle = "Euclidean Geometry", status = "Locked", lessonsCount = 6),
            LessonChapterEntity(subjectId = "mathematics", chapterNumber = "04", chapterTitle = "Probability & Stats", status = "Locked", lessonsCount = 5)
        )
        dao.insertChapters(initialChapters)

        // 4. Default Profile
        val defaultProfile = ProfileEntity(
            streakDays = 12,
            courseCompletion = 75,
            nextMilestone = "Bronze Sage",
            milestonePercentage = 85,
            offlineMode = false
        )
        dao.insertProfile(defaultProfile)
    }

    // State Mutation Methods
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun registerStudent(name: String, emailOrPhone: String, classLevel: Int, village: String) {
        viewModelScope.launch {
            val defaultProfile = ProfileEntity(
                id = 1,
                streakDays = 1,
                courseCompletion = 0,
                nextMilestone = "Village Scholar",
                milestonePercentage = 5,
                offlineMode = false,
                studentName = name,
                villageName = village,
                classSelected = classLevel,
                emailOrPhone = emailOrPhone,
                isLoggedIn = true
            )
            dao.insertProfile(defaultProfile)
            _selectedClass.value = classLevel
            navigateTo(Screen.MainApp)
        }
    }

    fun loginStudent(emailOrPhone: String, classLevel: Int) {
        viewModelScope.launch {
            val current = dao.getProfileFlow().first()
            if (current != null) {
                val updated = current.copy(
                    isLoggedIn = true,
                    emailOrPhone = emailOrPhone,
                    classSelected = classLevel
                )
                dao.updateProfile(updated)
                _selectedClass.value = classLevel
            } else {
                val newProfile = ProfileEntity(
                    id = 1,
                    streakDays = 1,
                    courseCompletion = 5,
                    nextMilestone = "Village Scholar",
                    milestonePercentage = 10,
                    offlineMode = false,
                    studentName = "Student",
                    villageName = "Punjab Region",
                    classSelected = classLevel,
                    emailOrPhone = emailOrPhone,
                    isLoggedIn = true
                )
                dao.insertProfile(newProfile)
                _selectedClass.value = classLevel
            }
            navigateTo(Screen.MainApp)
        }
    }

    fun logoutStudent() {
        viewModelScope.launch {
            val current = dao.getProfileFlow().first()
            if (current != null) {
                val updated = current.copy(isLoggedIn = false)
                dao.updateProfile(updated)
            }
            _currentScreen.value = Screen.Auth
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun selectClass(classNumber: Int) {
        _selectedClass.value = classNumber
        viewModelScope.launch {
            val current = profile.value
            if (current != null) {
                dao.updateProfile(current.copy(classSelected = classNumber))
            } else {
                loadClassSubjects(classNumber)
            }
        }
    }

    fun setSubjectId(subjectId: String) {
        _selectedSubjectId.value = subjectId
        syncSubjectChapters(subjectId)
    }

    // Offline State Toggle updates Room DB entry immediately preserving user settings
    fun toggleOfflineMode() {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            val updated = current.copy(offlineMode = !current.offlineMode)
            dao.updateProfile(updated)
        }
    }

    // Quiz Navigation & Grading State Machine
    fun startQuiz(
        subjectId: String,
        chapterNumber: String = "01",
        chapterTitle: String = "Introduction",
        difficulty: String = "Easy"
    ) {
        _quizSubjectId.value = subjectId
        _quizChapterNumber.value = chapterNumber
        _quizChapterTitle.value = chapterTitle
        _quizDifficulty.value = difficulty
        
        viewModelScope.launch {
            _quizQuestions.value = repository.getOrGenerateQuizQuestions(subjectId, chapterNumber, difficulty)
        }
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _quizSecondsElapsed.value = 0
        navigateTo(Screen.Quiz(subjectId))
    }

    fun selectAnswer(optionLetter: String) {
        val updated = _selectedAnswers.value.toMutableMap()
        updated[_currentQuestionIndex.value] = optionLetter
        _selectedAnswers.value = updated
    }

    fun nextQuestion(timeSpentSeconds: Int = 0) {
        if (_currentQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        } else {
            submitQuiz(timeSpentSeconds)
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitQuiz(timeSpentSeconds: Int) {
        val currentQuestions = _quizQuestions.value
        if (currentQuestions.isEmpty()) {
            navigateTo(Screen.SubjectDetail(_quizSubjectId.value))
            return
        }

        val answers = _selectedAnswers.value
        var correctCount = 0
        currentQuestions.forEachIndexed { index, q ->
            if (answers[index] == q.correctOption) {
                correctCount++
            }
        }
        val pct = (correctCount.toDouble() / currentQuestions.size.toDouble()) * 100.0

        viewModelScope.launch {
            // Unlocks master badge "math_wizard" if score is high (e.g. correctCount >= 4)
            if (correctCount >= 4 && _quizSubjectId.value == "mathematics") {
                val currentBadges = badges.value
                currentBadges.find { it.id == "math_wizard" }?.let { badge ->
                    if (badge.status == "LOCKED") {
                        dao.updateBadge(badge.copy(status = "UNLOCKED", unlockedAt = "Unlocked Today"))
                    }
                }
            }

            // Update user streak on completion
            dao.getProfileFlow().first()?.let { p ->
                dao.updateProfile(p.copy(streakDays = p.streakDays + 1))
            }

            // If the user gets at least 50% correct, mark this chapter as completed!
            if (pct >= 50.0) {
                repository.markChapterCompleted(_quizSubjectId.value ?: "mathematics", _quizChapterNumber.value ?: "01")
            }

            // Store results in Database
            val attempt = QuizAttemptEntity(
                subjectId = _quizSubjectId.value,
                chapterNumber = _quizChapterNumber.value,
                difficulty = _quizDifficulty.value,
                score = correctCount,
                totalQuestions = currentQuestions.size,
                percentage = pct,
                timeSpentSeconds = timeSpentSeconds,
                isSyncedToFirebase = false
            )

            // Save to Local Room db Cache
            dao.insertQuizAttempt(attempt)

            // Fulfills "Store results in Firebase"
            val firebaseSuccess = FirestoreManager.saveQuizAttempt(attempt)
            if (firebaseSuccess) {
                // If synced successfully, mark it in Local Room db
                val syncedAttempt = attempt.copy(isSyncedToFirebase = true)
                // Since autogenerated ID will be resolved, we can retrieve if needed or update (or let it query from db)
                // Let's just retrieve unsynced attempts and sync them
                syncUnsyncedAttempts()
            }
        }

        val min = timeSpentSeconds / 60
        val sec = timeSpentSeconds % 60
        navigateTo(Screen.QuizResult(score = correctCount, total = currentQuestions.size, timeMinutes = min, timeSeconds = sec))
    }

    /**
     * Sync any unsynced attempts from offline state to cloud Firestore
     */
    private suspend fun syncUnsyncedAttempts() {
        try {
            val unsyncedList = dao.getUnsyncedQuizAttempts()
            unsyncedList.forEach { attempt ->
                val success = FirestoreManager.saveQuizAttempt(attempt)
                if (success) {
                    dao.updateQuizAttempt(attempt.copy(isSyncedToFirebase = true))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Error backing up cached quiz attempts to cloud Firestore", e)
        }
    }
}
