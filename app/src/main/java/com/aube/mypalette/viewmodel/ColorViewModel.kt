package com.aube.mypalette.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.MyPaletteDatabase
import com.aube.mypalette.repository.ColorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColorViewModel(private val colorRepository: ColorRepository) : ViewModel() {
    val allColors: LiveData<List<ColorEntity>> = colorRepository.allColors
    private val _colorId = MutableLiveData<Int?>()
    val colorId: LiveData<Int?>
        get() = _colorId

    fun changeIdForColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result: Int? = colorRepository.changeIdForColor(color)
                _colorId.postValue(result)
            }
        }
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