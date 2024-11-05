package com.aube.mypalette.data.di

import com.aube.mypalette.data.database.ColorDao
import com.aube.mypalette.data.database.CombinationDao
import com.aube.mypalette.data.database.ImageDao
import com.aube.mypalette.data.database.MyPaletteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideColorDao(database: MyPaletteDatabase): ColorDao {
        return database.colorDao()
    }

    @Provides
    fun provideCombinationDao(database: MyPaletteDatabase): CombinationDao {
        return database.combinationDao()
    }

    @Provides
    fun provideImageDao(database: MyPaletteDatabase): ImageDao {
        return database.imageDao()
    }
}