package com.project981.dundun.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.ChangeNoticeLikeUseCase
import com.project981.dundun.domain.GetFollowNoticeListUseCase
import com.project981.dundun.model.dto.NoticeDisplayDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _list = MutableLiveData<List<NoticeDisplayDTO>>()
    val list : LiveData<List<NoticeDisplayDTO>>
        get() = _list
    private val followUseCase = GetFollowNoticeListUseCase()
    private val likeUseCase = ChangeNoticeLikeUseCase()
    fun getFollowNoticeList(){
        viewModelScope.launch(Dispatchers.IO) {
            followUseCase {
                _list.postValue(it)
            }
        }
    }

    fun changeNoticeLike(noticeID : String, isLike : Boolean, callback : (Boolean) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            likeUseCase(noticeID, isLike, callback)
        }
    }
}