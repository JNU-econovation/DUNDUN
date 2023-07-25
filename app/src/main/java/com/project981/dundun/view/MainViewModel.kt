package com.project981.dundun.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.GetUserIsArtistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val useCase = GetUserIsArtistUseCase()
    var key: Int = 0
    private var isArtist: String? = null
    private var focusArtistID = mutableMapOf<Int, String>()
    private var focusItem = mutableMapOf<Int, String>()
    private var editFocus : String? = null
    fun setFocus(artistId: String, item: String) {
        focusArtistID[key] = artistId
        focusItem[key] = item
    }

    fun getFocus(): Pair<String, String> {
        return Pair("M5hlj2NdszKLg2iIVM6j","")
    }

    fun setIsArtist() {
        viewModelScope.launch(Dispatchers.IO) {
            useCase {
                isArtist = it
            }
        }
    }

    fun getIsArtist(): String? {
        return isArtist
    }

    fun setEditFocus(edit : String?){
        editFocus = edit
    }

    fun getEditFocus():String?{
        return editFocus
    }


}