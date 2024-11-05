package com.aube.mypalette.data.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.data.database.ImageDao
import com.aube.mypalette.data.model.ImageEntity

class ImageRepository(private val imageDao: ImageDao) {
    fun getImagesForColor(colorId: Int): LiveData<List<ImageEntity>> {
        return imageDao.getImagesForColor(colorId)
    }

    suspend fun insert(image: ImageEntity) {
        imageDao.insertImageIfNotExists(image)
    }

    suspend fun delete(image: ImageEntity) {
        imageDao.deleteImage(image)
    }
}