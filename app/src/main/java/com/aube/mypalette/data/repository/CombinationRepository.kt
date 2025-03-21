package com.aube.mypalette.data.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.data.database.CombinationDao
import com.aube.mypalette.data.model.CombinationEntity

class CombinationRepository(private val combinationDao: CombinationDao) {
    val allCombinations: LiveData<List<CombinationEntity>> = combinationDao.getAllCombinations()

    suspend fun getCombination(id: Int): CombinationEntity {
        return combinationDao.getCombination(id)
    }

    suspend fun insert(combination: CombinationEntity) {
        combinationDao.insertCombination(combination)
    }

    suspend fun delete(combinationId: Int) {
        combinationDao.deleteCombination(combinationId)
    }
}