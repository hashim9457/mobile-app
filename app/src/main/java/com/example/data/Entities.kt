package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val category: String,
    val title: String,
    val progress: Int,
    val isOfflineReady: Boolean,
    val imageUrl: String,
    val iconName: String
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val tier: String,
    val status: String, // "UNLOCKED", "LOCKED"
    val unlockedAt: String?,
    val studentsCount: String,
    val iconName: String
)

@Entity(tableName = "chapters")
data class LessonChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: String,
    val chapterNumber: String,
    val chapterTitle: String,
    val status: String, // "Completed", "In Progress", "Locked"
    val lessonsCount: Int
)

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val streakDays: Int,
    val courseCompletion: Int,
    val nextMilestone: String,
    val milestonePercentage: Int,
    val offlineMode: Boolean = false, // Stores if offline toggle is active
    val studentName: String = "Ali Hashim",
    val villageName: String = "Punjab Region",
    val classSelected: Int = 4,
    val emailOrPhone: String = "alihashim7227@gmail.com",
    val isLoggedIn: Boolean = false
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val formula: String?,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String, // format: "subjectId_chapterNumber" e.g., "mathematics_01"
    val subjectId: String,
    val chapterNumber: String,
    val title: String,
    val content: String,
    val keywords: String, // comma-separated terms to assist quick study
    val totalReadingTimeMinutes: Int = 5,
    val isBookmarked: Boolean = false,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: String,
    val chapterNumber: String,
    val difficulty: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Double,
    val timeSpentSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isSyncedToFirebase: Boolean = false
)

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey val level: Int,
    val name: String,
    val iconName: String,
    val description: String
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionId: Int,
    val subjectId: String,
    val chapterNumber: String,
    val difficulty: String,
    val question: String,
    val formula: String?,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String
)



