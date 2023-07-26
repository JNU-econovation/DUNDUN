package com.project981.dundun.view.setting

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.UpdateArtistInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel:ViewModel() {
    val changeUseCase = UpdateArtistInfoUseCase()
    fun changeArtistInfo(name: String, bitmap: Bitmap, description: String, artistId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
            changeUseCase(name, bitmap, description, artistId, callback)
        }
    }
}