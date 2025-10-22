package com.aube.mypalette.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.CombinationEntity
import com.aube.mypalette.data.model.ImageEntity

@Dao
interface ColorDao {
    @Query("SELECT * FROM colors ORDER BY color")
    fun getAllColors(): LiveData<List<ColorEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertColor(color: ColorEntity): Long
    // Long을 반환하면 성공적으로 삽입된 행의 ID를 반환함

    @Delete
    suspend fun deleteColor(color: ColorEntity)
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
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE colorId = :colorId")
    fun getImagesByColorId(colorId: Int): LiveData<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE hash = :hash AND colorId = :colorId")
    suspend fun getImageByHash(hash: String, colorId: Int): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)
}