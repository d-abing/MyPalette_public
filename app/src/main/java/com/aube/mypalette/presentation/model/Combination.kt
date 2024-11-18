package com.aube.mypalette.presentation.model

import com.aube.mypalette.data.model.CombinationEntity

data class Combination(
    val id: Int,
    val colors: List<Int>,
    var isSelected: Boolean,
)

fun CombinationEntity.toUiModel(): Combination {
    return Combination(
        id = id,
        colors = colors,
        isSelected = false,
    )
}