package com.aube.mypalette.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.CombinationDao
import com.aube.mypalette.database.CombinationEntity
import com.aube.mypalette.database.MyPaletteDatabase

class CombinationRepository(private val combinationDao: CombinationDao) {
    val allCombinations: LiveData<List<CombinationEntity>> = combinationDao.getAllCombinations()

    suspend fun insert(combination: CombinationEntity) {
        combinationDao.insertCombination(combination)
    }

    suspend fun delete(combinationId: Int) {
        combinationDao.deleteCombination(combinationId)
    }
}