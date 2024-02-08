package com.aube.mypalette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.repository.ColorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColorViewModel(private val colorRepository: ColorRepository) : ViewModel() {
    val allColors: LiveData<List<ColorEntity>> = colorRepository.allColors
    private val _colorId = MutableLiveData<Int?>()
    val colorId: LiveData<Int?>
        get() = _colorId

    fun setIdNull() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _colorId.postValue(null)
            }
        }
    }

    fun checkIdForColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result: Int? = colorRepository.checkIdForColor(color)
                _colorId.postValue(result)
            }
        }
    }

    fun insert(color: ColorEntity) {
        viewModelScope.launch {
            colorRepository.insert(color)

            withContext(Dispatchers.IO) {
                val result: Int? = colorRepository.checkIdForColor(color.color)
                _colorId.postValue(result)
            }
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