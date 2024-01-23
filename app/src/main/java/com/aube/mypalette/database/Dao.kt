package com.aube.mypalette.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {
    @Query("SELECT * FROM colors")
    fun getAllColors(): LiveData<List<ColorEntity>>

    @Query("SELECT id FROM colors WHERE color = :colorValue")
    fun getIdForColor(colorValue: Int): Int?

    // 실제 insert 메서드
    suspend fun insertColorIfNotExists(color: ColorEntity) {
        val existingId = getIdForColor(color.color)
        if (existingId == null) {
            insertColor(color)
        }
    }

    @Insert
    suspend fun insertColor(color: ColorEntity)

    @Delete
    suspend fun deleteColor(color: ColorEntity)
}

@Dao
interface CombinationDao {
    @Query("SELECT * FROM combinations")
    fun getAllCombinations(): LiveData<List<CombinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCombination(combination: CombinationEntity)

    @Delete
    suspend fun deleteCombination(combination: CombinationEntity)
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE colorId = :colorId")
    fun getImagesForColor(colorId: Int): LiveData<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)
}