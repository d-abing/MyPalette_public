package com.aube.mypalette.data

import com.aube.mypalette.model.ColorItem
import com.aube.mypalette.model.CombinationItem

class ColorRepository {
    // 색 데이터 관리
    private val colorList: MutableList<ColorItem> = mutableListOf()
    private val combinationList: MutableList<CombinationItem> = mutableListOf()

    fun addColor(colorItem: ColorItem) {
        colorList.add(colorItem)
    }

    fun getAllColors(): List<ColorItem> {
        return colorList
    }

    fun addCombination(combinationItem: CombinationItem) {
        combinationList.add(combinationItem)
    }

    fun getAllCombinations(): List<CombinationItem> {
        return combinationList
    }
}