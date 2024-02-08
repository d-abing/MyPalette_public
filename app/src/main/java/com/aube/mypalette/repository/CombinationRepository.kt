package com.aube.mypalette.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.database.CombinationDao
import com.aube.mypalette.database.CombinationEntity

class CombinationRepository(private val combinationDao: CombinationDao) {
    val allCombinations: LiveData<List<CombinationEntity>> = combinationDao.getAllCombinations()

    suspend fun insert(combination: CombinationEntity) {
        combinationDao.insertCombination(combination)
    }

    suspend fun delete(combinationId: Int) {
        combinationDao.deleteCombination(combinationId)
    }
}