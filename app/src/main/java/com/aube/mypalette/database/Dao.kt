package com.aube.mypalette.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ColorDao {
    @Query("SELECT * FROM colors ORDER BY color")
    fun getAllColors(): LiveData<List<ColorEntity>>

    @Query("SELECT id FROM colors WHERE color = :colorValue")
    suspend fun checkIdForColor(colorValue: Int): Int?

    // 실제 insert 메서드
    suspend fun insertColorIfNotExists(color: ColorEntity) {
        val existingId = checkIdForColor(color.color)
        Log.d("test다", "color insert 시도")
        if (existingId == null) {
            Log.d("test다", "color insert 성공")
            insertColor(color)
        } else {
            Log.d("test다", "color insert 실패")
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

    @Query("DELETE FROM combinations WHERE id = :combinationId")
    suspend fun deleteCombination(combinationId: Int)
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
        Log.d("test다", "image insert 시도")
        if (existingId == null) {
            Log.d("test다", "image insert 성공")
            insertImage(image)
        } else {
            Log.d("test다", "image insert 실패")
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)
}