package com.project981.dundun.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.GetUserIsArtistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    lateinit var callback: () -> Int
    val useCase = GetUserIsArtistUseCase()
    var _isArtist = MutableLiveData<String?>(null)
    var focusArtist : String? = null
    var focusItem : String? = null
    var editFocus : String? = null
    val _progress = MutableLiveData(false)
    fun setIsArtist() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase {
                _isArtist.postValue(it)
            }
        }
    }

    fun setProgress(visible : Boolean){
        _progress.value = visible
    }


}