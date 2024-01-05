package com.aube.mypalette.viewmodel

import androidx.lifecycle.ViewModel
import com.aube.mypalette.data.ColorRepository
import com.aube.mypalette.model.ColorItem

class ColorViewModel : ViewModel() {
    private val colorRepository = ColorRepository()

    fun addColor(colorItem: ColorItem) {
        colorRepository.addColor(colorItem)
    }

    fun getAllColors(): List<ColorItem> {
        return colorRepository.getAllColors()
    }
}