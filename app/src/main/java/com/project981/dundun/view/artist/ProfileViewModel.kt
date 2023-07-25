package com.project981.dundun.view.artist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.ChangeArtistFollowUseCase
import com.project981.dundun.domain.ChangeNoticeLikeUseCase
import com.project981.dundun.domain.GetArtistIsFollowUseCase
import com.project981.dundun.domain.GetArtistNoticeListUseCase
import com.project981.dundun.domain.GetProfileTopInfoUseCase
import com.project981.dundun.model.dto.ProfileTopDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val noticeUseCase = GetArtistNoticeListUseCase()
    private val followUseCase = ChangeArtistFollowUseCase()
    private val isFollowUseCase = GetArtistIsFollowUseCase()
    private val likeUseCase = ChangeNoticeLikeUseCase()
    private val profileTopUseCase = GetProfileTopInfoUseCase()

    //TODO 캡슐화
    val _list = MutableLiveData<List<Any>>()
    fun changeNoticeLike(noticeID: String, isLike: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            likeUseCase(noticeID, isLike, callback)
        }
    }

    fun getArtistNotice(profileTopDTO: ProfileTopDTO, artistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noticeUseCase(artistId) {
                val tempList = mutableListOf<Any>()
                tempList.add(profileTopDTO)
                tempList.addAll(it)
                _list.postValue(tempList)
            }
        }
    }

    fun changeArtistFollow(artistId: String, isFollow: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            followUseCase(artistId, isFollow, callback)
        }
    }

    fun getArtistIsFollow(artistId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isFollowUseCase(artistId, callback)
        }
    }

    fun getProfileTop(artistId: String, callback: (ProfileTopDTO) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            profileTopUseCase(artistId, callback)
        }
    }
}