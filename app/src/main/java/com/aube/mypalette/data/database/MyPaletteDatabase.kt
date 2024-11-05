package com.aube.mypalette.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.CombinationEntity
import com.aube.mypalette.data.model.ImageEntity

@Database(
    entities = [ColorEntity::class, CombinationEntity::class, ImageEntity::class],
    version = 1
)
@TypeConverters(ColorListConverter::class)
abstract class MyPaletteDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao
    abstract fun combinationDao(): CombinationDao
    abstract fun imageDao(): ImageDao
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