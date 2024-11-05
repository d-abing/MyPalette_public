package com.aube.mypalette.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.repository.ColorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ColorViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
) : ViewModel() {
    val allColors: LiveData<List<ColorEntity>> = colorRepository.allColors
    private val _colorId = MutableLiveData<Int?>()
    val colorId: LiveData<Int?>
        get() = _colorId

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

    fun delete(colorId: Int) {
        viewModelScope.launch {
            colorRepository.delete(colorId)
        }
    }
}