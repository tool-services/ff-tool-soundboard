package com.fftool.soundboard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SoundEntity::class], version = 1, exportSchema = false)
abstract class SoundDatabase : RoomDatabase() {
    abstract fun soundDao(): SoundDao

    companion object {
        @Volatile
        private var INSTANCE: SoundDatabase? = null

        fun getDatabase(context: Context): SoundDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundDatabase::class.java,
                    "ff_tool_soundboard.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
