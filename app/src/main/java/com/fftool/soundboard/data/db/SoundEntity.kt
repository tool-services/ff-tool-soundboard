package com.fftool.soundboard.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sounds",
    indices = [Index(value = ["box_number"], unique = true)]
)
data class SoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "box_number") val boxNumber: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "upload_date") val uploadDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_custom_name") val isCustomName: Boolean = false
)
