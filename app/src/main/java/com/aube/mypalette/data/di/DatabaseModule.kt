package com.aube.mypalette.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE UNIQUE INDEX index_images_hash_colorId ON images(hash, colorId)")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MyPaletteDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            MyPaletteDatabase::class.java,
            "my_palette_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}
