package com.aube.mypalette.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.MyPaletteDatabase
import com.aube.mypalette.repository.ColorRepository
import kotlinx.coroutines.launch

class ColorViewModel(private val colorRepository: ColorRepository) : ViewModel() {
    val allColors: LiveData<List<ColorEntity>> = colorRepository.allColors

    fun getIdForColor(color: Int): Int? {
        return colorRepository.getIdForColor(color)
    }

    fun insert(color: ColorEntity) {
        viewModelScope.launch {
            colorRepository.insert(color)
        }
    }

    fun delete(color: ColorEntity) {
        viewModelScope.launch {
            colorRepository.delete(color)
        }
    }
}

class ColorViewModelFactory(private val repository: ColorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}