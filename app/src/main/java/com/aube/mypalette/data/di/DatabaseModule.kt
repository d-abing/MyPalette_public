package com.aube.mypalette.data.di

import android.content.Context
import androidx.room.Room
import com.aube.mypalette.data.database.MyPaletteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MyPaletteDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            MyPaletteDatabase::class.java,
            "my_palette_database"
        ).build()
    }
}
