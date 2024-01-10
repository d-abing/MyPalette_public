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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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