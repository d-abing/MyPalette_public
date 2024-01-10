package com.aube.mypalette.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.database.ColorDao
import com.aube.mypalette.database.ColorEntity

class ColorRepository(private val colorDao: ColorDao) {
    val allColors: LiveData<List<ColorEntity>> = colorDao.getAllColors()

    suspend fun insert(color: ColorEntity) {
        colorDao.insertColor(color)
    }

    suspend fun delete(color: ColorEntity) {
        colorDao.deleteColor(color)
    }
}
