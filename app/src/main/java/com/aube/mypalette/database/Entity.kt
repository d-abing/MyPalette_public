package com.aube.mypalette.database

import androidx.room.Entity
import androidx.room.ForeignKey
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

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = ColorEntity::class,
            parentColumns = ["id"],
            childColumns = ["colorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageBytes: ByteArray,
    val colorId: Int // 외래키
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (id != other.id) return false
        if (!imageBytes.contentEquals(other.imageBytes)) return false
        return colorId == other.colorId
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageBytes.contentHashCode()
        result = 31 * result + colorId
        return result
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