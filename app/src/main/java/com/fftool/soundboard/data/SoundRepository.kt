package com.fftool.soundboard.data

import android.content.Context
import android.net.Uri
import com.fftool.soundboard.data.db.SoundDao
import com.fftool.soundboard.data.db.SoundDatabase
import com.fftool.soundboard.data.db.SoundEntity
import kotlinx.coroutines.flow.Flow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class SoundRepository(private val context: Context) {
    private val dao: SoundDao = SoundDatabase.getDatabase(context).soundDao()

    val allSounds: Flow<List<SoundEntity>> = dao.getAllSounds()
    val allSoundsByFavorite: Flow<List<SoundEntity>> = dao.getAllSoundsByFavorite()

    fun getSoundsDir(): File {
        val dir = File(context.filesDir, "sounds")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getImagesDir(): File {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun getNextBoxNumber(): Int = dao.getMaxBoxNumber() + 1

    suspend fun importSound(
        uri: Uri,
        displayName: String,
        imageUri: Uri? = null,
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

        val imagePath = imageUri?.let { saveImage(it, uuid) }

        val entity = SoundEntity(
            boxNumber = boxNum,
            displayName = displayName,
            filePath = destFile.absolutePath,
            imagePath = imagePath,
            isCustomName = isCustomName
        )
        dao.insert(entity)
        return entity
    }

    suspend fun importSoundFromPath(
        sourcePath: String,
        displayName: String,
        imagePath: String? = null,
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

        val finalImagePath = imagePath ?: copyExistingImage(sourcePath, uuid)

        val entity = SoundEntity(
            boxNumber = boxNum,
            displayName = displayName,
            filePath = destFile.absolutePath,
            imagePath = finalImagePath,
            isCustomName = isCustomName
        )
        dao.insert(entity)
        return entity
    }

    private fun saveImage(uri: Uri, uuid: String): String {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val scaled = scaleToSquare(bitmap, 300)
        val destFile = File(getImagesDir(), "${uuid}_img.jpg")
        FileOutputStream(destFile).use { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        return destFile.absolutePath
    }

    private fun copyExistingImage(sourcePath: String, uuid: String): String? {
        val baseName = File(sourcePath).nameWithoutExtension
        val possibleImages = getImagesDir().listFiles()
            ?.filter { it.name.contains(baseName, ignoreCase = true) }
        if (possibleImages.isNullOrEmpty()) return null
        val src = possibleImages.first()
        val dest = File(getImagesDir(), "${uuid}_img.jpg")
        src.copyTo(dest, overwrite = true)
        return dest.absolutePath
    }

    private fun scaleToSquare(bitmap: Bitmap, size: Int): Bitmap {
        val dim = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - dim) / 2
        val y = (bitmap.height - dim) / 2
        val cropped = Bitmap.createBitmap(bitmap, x.coerceAtLeast(0), y.coerceAtLeast(0), dim, dim)
        return Bitmap.createScaledBitmap(cropped, size, size, true)
    }

    suspend fun deleteSound(boxNumber: Int) {
        val sound = dao.getSoundByBoxNumber(boxNumber)
        if (sound != null) {
            File(sound.filePath).delete()
            sound.imagePath?.let { File(it).delete() }
            dao.deleteByBoxNumber(boxNumber)
        }
    }

    suspend fun deleteAll() {
        val all = dao.getAllSoundsList()
        all.forEach {
            File(it.filePath).delete()
            it.imagePath?.let { path -> File(path).delete() }
        }
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
