package com.fftool.soundboard.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundDao {
    @Query("SELECT * FROM sounds ORDER BY box_number ASC")
    fun getAllSounds(): Flow<List<SoundEntity>>

    @Query("SELECT * FROM sounds ORDER BY box_number ASC")
    suspend fun getAllSoundsList(): List<SoundEntity>

    @Query("SELECT * FROM sounds WHERE box_number = :boxNumber")
    suspend fun getSoundByBoxNumber(boxNumber: Int): SoundEntity?

    @Query("SELECT COALESCE(MAX(box_number), 0) FROM sounds")
    suspend fun getMaxBoxNumber(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sound: SoundEntity): Long

    @Update
    suspend fun update(sound: SoundEntity)

    @Delete
    suspend fun delete(sound: SoundEntity)

    @Query("DELETE FROM sounds WHERE box_number = :boxNumber")
    suspend fun deleteByBoxNumber(boxNumber: Int)

    @Query("DELETE FROM sounds")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sounds")
    suspend fun getCount(): Int
}
