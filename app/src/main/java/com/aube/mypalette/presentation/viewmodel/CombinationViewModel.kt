package com.aube.mypalette.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.data.model.CombinationEntity
import com.aube.mypalette.data.repository.CombinationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CombinationViewModel @Inject constructor(
    private val combinationRepository: CombinationRepository,
) : ViewModel() {
    val allCombinations: LiveData<List<CombinationEntity>> = combinationRepository.allCombinations

    private val _combination = MutableLiveData<CombinationEntity>()
    val combination: LiveData<CombinationEntity> = _combination

    fun insert(combination: CombinationEntity) {
        viewModelScope.launch {
            combinationRepository.insert(combination)
        }
    }

    fun delete(combinationId: Int) {
        viewModelScope.launch {
            combinationRepository.delete(combinationId)
        }
    }

    fun getCombination(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result: CombinationEntity = combinationRepository.getCombination(id)
                _combination.postValue(result)
            }
        }
    }
}