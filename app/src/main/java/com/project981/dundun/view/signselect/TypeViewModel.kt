package com.project981.dundun.view.signselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TypeViewModel: ViewModel() {
    private val _isArtist = MutableLiveData(false)
    val isArtist : LiveData<Boolean>
        get() = _isArtist

    fun setArtist(isArtist : Boolean){
        _isArtist.value = isArtist
    }
}