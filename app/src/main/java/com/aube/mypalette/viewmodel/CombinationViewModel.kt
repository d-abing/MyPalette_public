package com.aube.mypalette.viewmodel

import androidx.lifecycle.ViewModel
import com.aube.mypalette.data.ColorRepository
import com.aube.mypalette.model.CombinationItem

class CombinationViewModel : ViewModel() {
    private val colorRepository = ColorRepository()

    fun addCombination(combinationItem: CombinationItem) {
        colorRepository.addCombination(combinationItem)
    }

    fun getAllCombinations(): List<CombinationItem> {
        return colorRepository.getAllCombinations()
    }
}
