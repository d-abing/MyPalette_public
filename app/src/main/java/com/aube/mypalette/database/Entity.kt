package com.aube.mypalette.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int
)

@Entity(tableName = "combinations")
data class CombinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val colors: String // 조합에 속한 색의 ID 목록
)

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