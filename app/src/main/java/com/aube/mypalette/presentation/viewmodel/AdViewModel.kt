package com.aube.mypalette.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor() : ViewModel() {
    val isAdShown = MutableLiveData(false)

    fun markAdAsShown() {
        isAdShown.value = true
    }

    fun resetAdState() {
        isAdShown.value = false
    }
}