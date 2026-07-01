package com.fftool.soundboard.data.models

data class Sound(
    val id: Long = 0,
    val boxNumber: Int,
    val displayName: String,
    val filePath: String,
    val uploadDate: Long = System.currentTimeMillis(),
    val isCustomName: Boolean = false
)
