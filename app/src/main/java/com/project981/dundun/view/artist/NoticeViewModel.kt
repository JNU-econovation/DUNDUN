package com.project981.dundun.view.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project981.dundun.domain.CreateNoticeUseCase
import com.project981.dundun.model.dto.NoticeCreateDTO

class NoticeViewModel : ViewModel() {
    private val useCase = CreateNoticeUseCase()
    private val _yearMonthDay = MutableLiveData<Pair<Int, Int>>()
    val yearMonthDay : LiveData<Pair<Int, Int>>
        get() = _yearMonthDay

    private val _eventList = MutableLiveData<List<NoticeCreateDTO>?>()
    val eventList : LiveData<List<NoticeCreateDTO>?>
        get() = _eventList

}