package com.aube.mypalette.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ColorDao {
    @Query("SELECT * FROM colors ORDER BY color")
    fun getAllColors(): LiveData<List<ColorEntity>>

    @Query("SELECT id FROM colors WHERE color = :colorValue")
    suspend fun checkIdForColor(colorValue: Int): Int?

    // 실제 insert 메서드
    suspend fun insertColorIfNotExists(color: ColorEntity) {
        val existingId = checkIdForColor(color.color)
        if (existingId == null) {
            insertColor(color)
        } else {
        }
    }

    @Insert
    suspend fun insertColor(color: ColorEntity)

    @Query("DELETE FROM colors WHERE id = :colorId")
    suspend fun deleteColor(colorId: Int)
}

@Dao
interface CombinationDao {
    @Query("SELECT * FROM combinations")
    fun getAllCombinations(): LiveData<List<CombinationEntity>>

    @Query("SELECT * FROM combinations WHERE id = :id")
    suspend fun getCombination(id: Int): CombinationEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCombination(combination: CombinationEntity)

    @Query("DELETE FROM combinations WHERE id = :combinationId")
    suspend fun deleteCombination(combinationId: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCombination(combination: CombinationEntity)
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE colorId = :colorId")
    fun getImagesForColor(colorId: Int): LiveData<List<ImageEntity>>

    @Query("SELECT id FROM images WHERE imageBytes = :imageBytes and colorId = :colorId")
    suspend fun checkIdForImage(imageBytes: ByteArray, colorId: Int): Int?

    // 실제 insert 메서드
    suspend fun insertImageIfNotExists(image: ImageEntity) {
        val existingId = checkIdForImage(image.imageBytes, image.colorId)
        if (existingId == null) {
            insertImage(image)
        } else {
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)
}