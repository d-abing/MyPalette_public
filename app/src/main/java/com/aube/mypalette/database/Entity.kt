package com.aube.mypalette.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "int_list")
    val colors: List<Int> // 조합에 속한 색의 ID 목록
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