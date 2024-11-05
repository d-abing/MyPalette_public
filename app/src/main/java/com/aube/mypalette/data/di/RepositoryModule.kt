package com.aube.mypalette.data.di

import com.aube.mypalette.data.database.ColorDao
import com.aube.mypalette.data.database.CombinationDao
import com.aube.mypalette.data.database.ImageDao
import com.aube.mypalette.data.repository.ColorRepository
import com.aube.mypalette.data.repository.CombinationRepository
import com.aube.mypalette.data.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    fun provideColorRepository(colorDao: ColorDao): ColorRepository {
        return ColorRepository(colorDao)
    }

    @Provides
    fun provideCombinationRepository(combinationDao: CombinationDao): CombinationRepository {
        return CombinationRepository(combinationDao)
    }

    @Provides
    fun provideImageRepository(imageDao: ImageDao): ImageRepository {
        return ImageRepository(imageDao)
    }
}