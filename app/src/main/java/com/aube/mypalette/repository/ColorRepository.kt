package com.aube.mypalette.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.aube.mypalette.database.ColorDao
import com.aube.mypalette.database.ColorEntity

class ColorRepository(private val colorDao: ColorDao) {
    val allColors: LiveData<List<ColorEntity>> = colorDao.getAllColors()

    fun getIdForColor(color: Int): Int? {
        return colorDao.getIdForColor(color)
    }

    suspend fun insert(color: ColorEntity) {
        colorDao.insertColor(color)
    }

    suspend fun delete(color: ColorEntity) {
        colorDao.deleteColor(color)
    }
}
