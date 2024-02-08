package com.aube.mypalette.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.database.ImageEntity
import com.aube.mypalette.repository.ImageRepository
import kotlinx.coroutines.launch

class ImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
    fun getImagesForColor(colorId: Int): LiveData<List<ImageEntity>> {
        return imageRepository.getImagesForColor(colorId)
    }

    fun insert(image: ImageEntity) {
        viewModelScope.launch {
            imageRepository.insert(image)
        }
    }

    fun delete(image: ImageEntity) {
        viewModelScope.launch {
            imageRepository.delete(image)
        }
    }
}

class ImageViewModelFactory(private val imageRepository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}