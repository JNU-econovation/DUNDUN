package com.project981.dundun.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.GetArtistTopByPrefixUseCase
import com.project981.dundun.model.dto.ProfileTopDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.security.auth.callback.Callback

class SearchViewModel : ViewModel() {
    private val useCase = GetArtistTopByPrefixUseCase()
    fun getArtistList(prefix: String, callback: (List<ProfileTopDTO>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase(prefix, callback)
        }
    }

}