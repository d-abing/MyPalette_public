package com.aube.mypalette.domain.usecase

import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.data.repository.ColorRepository
import com.aube.mypalette.data.repository.ImageRepository

class InsertImageWithColorUseCase(
    private val colorRepository: ColorRepository,
    private val imageRepository: ImageRepository,
) {
    suspend operator fun invoke(color: Int, imageBytes: ByteArray) {
        val colorId = colorRepository.insert(ColorEntity(color = color))
        imageRepository.insert(ImageEntity(colorId = colorId, imageBytes = imageBytes))
    }
}