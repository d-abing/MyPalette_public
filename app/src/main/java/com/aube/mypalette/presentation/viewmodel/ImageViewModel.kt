package com.aube.mypalette.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.data.repository.ColorRepository
import com.aube.mypalette.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    fun getImagesForColor(colorId: Int): LiveData<List<ImageEntity>> {
        return imageRepository.getImagesForColor(colorId)
    }

    fun insert(color: Int, imageBytes: ByteArray) {
        viewModelScope.launch {
            val colorId = async { colorRepository.insert(ColorEntity(color = color)) }
            imageRepository.insert(ImageEntity(imageBytes = imageBytes, colorId = colorId.await()))
        }
    }

    fun delete(image: ImageEntity) {
        viewModelScope.launch {
            imageRepository.delete(image)
        }
    }
}