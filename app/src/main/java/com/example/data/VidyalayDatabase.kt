package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SubjectEntity::class,
        BadgeEntity::class,
        LessonChapterEntity::class,
        ProfileEntity::class,
        NoteEntity::class,
        QuizAttemptEntity::class,
        ClassEntity::class,
        QuizQuestionEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class VidyalayDatabase : RoomDatabase() {
    abstract val dao: VidyalayDao

    companion object {
        @Volatile
        private var INSTANCE: VidyalayDatabase? = null

        fun getInstance(context: Context): VidyalayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VidyalayDatabase::class.java,
                    "vidyalay_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
