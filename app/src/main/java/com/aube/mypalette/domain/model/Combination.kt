package com.aube.mypalette.domain.model

import com.aube.mypalette.data.model.CombinationEntity

data class Combination(
    val id: Int,
    val colors: List<Int>,
    var isSelected: Boolean,
)

fun CombinationEntity.toDomainModel(): Combination {
    return Combination(
        id = id,
        colors = colors,
        isSelected = false,
    )
}