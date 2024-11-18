package com.aube.mypalette.domain.di

import com.aube.mypalette.data.repository.ColorRepository
import com.aube.mypalette.data.repository.ImageRepository
import com.aube.mypalette.domain.usecase.InsertImageWithColorUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object UsecaseModule {

    @Provides
    fun provideInsertImageWithColorUseCase(
        colorRepository: ColorRepository,
        imageRepository: ImageRepository,
    ): InsertImageWithColorUseCase {
        return InsertImageWithColorUseCase(colorRepository, imageRepository)
    }
}