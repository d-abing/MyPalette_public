package com.aube.mypalette.data.repository

import androidx.lifecycle.LiveData
import com.aube.mypalette.data.database.ImageDao
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.utils.generateMD5Hash

class ImageRepository(private val imageDao: ImageDao) {
    fun getImagesByColorId(colorId: Int): LiveData<List<ImageEntity>> {
        return imageDao.getImagesByColorId(colorId)
    }

    suspend fun insert(image: ImageEntity) {
        val hash = generateMD5Hash(image.imageBytes)
        val existingImage = imageDao.getImageByHash(hash, image.colorId)

        if (existingImage == null) {
            val newImage = image.copy(hash = hash)
            imageDao.insertImage(newImage)
        }
    }

    suspend fun delete(image: ImageEntity) {
        imageDao.deleteImage(image)
    }
}