package com.aube.mypalette.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.database.CombinationEntity
import com.aube.mypalette.repository.CombinationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CombinationViewModel(private val combinationRepository: CombinationRepository) : ViewModel() {
    val allCombinations: LiveData<List<CombinationEntity>>  = combinationRepository.allCombinations
    private val _combination = MutableLiveData<CombinationEntity>()
    val combination: LiveData<CombinationEntity>
        get() = _combination

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

    fun update(combination: CombinationEntity) {
        viewModelScope.launch {
            combinationRepository.update(combination)
        }
    }
}

class CombinationViewModelFactory(private val repository: CombinationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CombinationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CombinationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}