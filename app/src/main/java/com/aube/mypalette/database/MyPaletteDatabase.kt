package com.aube.mypalette.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aube.mypalette.database.ColorDao
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.CombinationDao
import com.aube.mypalette.database.CombinationEntity

@Database(entities = [ColorEntity::class, CombinationEntity::class, ImageEntity::class], version = 1)
@TypeConverters(ColorListConverter::class)
abstract class MyPaletteDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao
    abstract fun combinationDao(): CombinationDao
    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: MyPaletteDatabase? = null

        fun getInstance(context: Context): MyPaletteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyPaletteDatabase::class.java,
                    "my_palette_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ColorListConverter {
    @TypeConverter
    fun fromList(colors: List<Int>?): String? {
        return colors?.joinToString(",")
    }

    @TypeConverter
    fun toList(colorsString: String?): List<Int>? {
        return colorsString?.split(",")?.map { it.toInt() }
    }
}