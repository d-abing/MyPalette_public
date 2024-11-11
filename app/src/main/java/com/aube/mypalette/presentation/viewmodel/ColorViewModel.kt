package com.aube.mypalette.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.repository.ColorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColorViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
) : ViewModel() {
    val allColors: LiveData<List<ColorEntity>> = colorRepository.allColors

    fun delete(color: ColorEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            colorRepository.delete(color)
        }
    }
}