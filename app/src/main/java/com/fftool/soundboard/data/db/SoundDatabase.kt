package com.fftool.soundboard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SoundEntity::class], version = 2, exportSchema = false)
abstract class SoundDatabase : RoomDatabase() {
    abstract fun soundDao(): SoundDao

    companion object {
        @Volatile
        private var INSTANCE: SoundDatabase? = null

        private val MIGRATION_1_2 = Migration(1, 2) { db ->
            db.execSQL("ALTER TABLE sounds ADD COLUMN image_path TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE sounds ADD COLUMN is_favorite INTEGER DEFAULT 0 NOT NULL")
        }

        fun getDatabase(context: Context): SoundDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundDatabase::class.java,
                    "ff_tool_soundboard.db"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
