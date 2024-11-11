package com.aube.mypalette.data.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.data.database.ColorDao
import com.aube.mypalette.data.model.ColorEntity

class ColorRepository(private val colorDao: ColorDao) {
    val allColors: LiveData<List<ColorEntity>> = colorDao.getAllColors()

    suspend fun insert(color: ColorEntity): Int {
        return colorDao.insertColorIfNotExists(color)
    }

    suspend fun delete(color: ColorEntity) {
        colorDao.deleteColor(color)
    }
}
