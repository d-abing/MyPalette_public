package com.aube.mypalette.data.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.data.database.ColorDao
import com.aube.mypalette.data.model.ColorEntity

class ColorRepository(private val colorDao: ColorDao) {
    val allColors: LiveData<List<ColorEntity>> = colorDao.getAllColors()

    suspend fun checkIdForColor(color: Int): Int? {
        return colorDao.checkIdForColor(color)
    }

    suspend fun insert(color: ColorEntity) {
        colorDao.insertColorIfNotExists(color)
    }

    suspend fun delete(colorId: Int) {
        colorDao.deleteColor(colorId)
    }
}