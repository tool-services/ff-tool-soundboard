package com.fftool.soundboard.data

import android.content.Context
import android.net.Uri
import com.fftool.soundboard.data.db.SoundDao
import com.fftool.soundboard.data.db.SoundDatabase
import com.fftool.soundboard.data.db.SoundEntity
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class SoundRepository(private val context: Context) {
    private val dao: SoundDao = SoundDatabase.getDatabase(context).soundDao()

    val allSounds: Flow<List<SoundEntity>> = dao.getAllSounds()

    fun getSoundsDir(): File {
        val dir = File(context.filesDir, "sounds")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun getNextBoxNumber(): Int = dao.getMaxBoxNumber() + 1

    suspend fun importSound(
        uri: Uri,
        displayName: String,
        boxNumber: Int? = null,
        isCustomName: Boolean = false
    ): SoundEntity {
        val boxNum = boxNumber ?: getNextBoxNumber()
        val uuid = UUID.randomUUID().toString()
        val ext = getFileExtension(context, uri)
        val fileName = "$uuid.$ext"
        val destFile = File(getSoundsDir(), fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Cannot open input stream for URI: $uri")

        val entity = SoundEntity(
            boxNumber = boxNum,
            displayName = displayName,
            filePath = destFile.absolutePath,
            isCustomName = isCustomName
        )
        dao.insert(entity)
        return entity
    }

    suspend fun importSoundFromPath(
        sourcePath: String,
        displayName: String,
        boxNumber: Int? = null,
        isCustomName: Boolean = false
    ): SoundEntity {
        val boxNum = boxNumber ?: getNextBoxNumber()
        val uuid = UUID.randomUUID().toString()
        val ext = sourcePath.substringAfterLast('.', "mp3")
        val fileName = "$uuid.$ext"
        val destFile = File(getSoundsDir(), fileName)

        File(sourcePath).inputStream().use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        val entity = SoundEntity(
            boxNumber = boxNum,
            displayName = displayName,
            filePath = destFile.absolutePath,
            isCustomName = isCustomName
        )
        dao.insert(entity)
        return entity
    }

    suspend fun deleteSound(boxNumber: Int) {
        val sound = dao.getSoundByBoxNumber(boxNumber)
        if (sound != null) {
            File(sound.filePath).delete()
            dao.deleteByBoxNumber(boxNumber)
        }
    }

    suspend fun deleteAll() {
        val all = dao.getAllSoundsList()
        all.forEach { File(it.filePath).delete() }
        dao.deleteAll()
    }

    suspend fun getCount(): Int = dao.getCount()

    companion object {
        fun getFileExtension(context: Context, uri: Uri): String {
            val mime = context.contentResolver.getType(uri) ?: "audio/mpeg"
            return when {
                mime.contains("mpeg") -> "mp3"
                mime.contains("wav") -> "wav"
                mime.contains("m4a") -> "m4a"
                mime.contains("ogg") -> "ogg"
                mime.contains("flac") -> "flac"
                mime.contains("aac") -> "aac"
                else -> "mp3"
            }
        }

        fun isAudioFile(name: String): Boolean {
            val ext = name.substringAfterLast('.', "").lowercase()
            return ext in listOf("mp3", "wav", "m4a", "ogg", "flac", "aac")
        }
    }
}
