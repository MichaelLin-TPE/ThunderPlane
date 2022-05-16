package com.thunder.thunderplane.level_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thunder.thunderplane.bean.LevelData

class LevelViewModel(private val repository: LevelRepository) : ViewModel() {

    private val _leveDataListLiveData = MutableLiveData<MutableList<LevelData>>()
    val levelDataListLiveData :LiveData<MutableList<LevelData>> = _leveDataListLiveData

    fun onActivityCreate() {
        _leveDataListLiveData.value = repository.getLevelDataList()
    }

    class LevelViewModelFactory(private val repository: LevelRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LevelViewModel(repository) as T
        }
    }

}