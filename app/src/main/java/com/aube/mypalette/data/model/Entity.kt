package com.aube.mypalette.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "colors",
    indices = [Index(value = ["color"], unique = true)]
)
data class ColorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int,
)

@Entity(tableName = "combinations")
data class CombinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "int_list")
    val colors: List<Int>, // 조합에 속한 색의 ID 목록
)

@Entity(
    tableName = "images",
    indices = [Index(value = ["hash", "colorId"], unique = true)],
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
    val hash: String = "",
    val imageBytes: ByteArray,
    val colorId: Int, // 외래키
)