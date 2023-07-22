package com.project981.dundun.view.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.GetEventListByMonthAndYearUseCase
import com.project981.dundun.model.dto.BottomDetailDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    val useCase = GetEventListByMonthAndYearUseCase()
    private val _yearAndMonthLive = MutableLiveData<Pair<Int,Int>>()
    val yearAndMonthLive : LiveData<Pair<Int,Int>>
        get() = _yearAndMonthLive

    private val _evenList = MutableLiveData<List<BottomDetailDTO>?>()
    val eventList : LiveData<List<BottomDetailDTO>?>
        get() = _evenList

    fun getEventList(month : Int, year:Int , callback : (List<List<BottomDetailDTO>>) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            useCase(month, year, callback)
        }
    }

    fun setYearAndMonth(month: Int,year: Int){
        _yearAndMonthLive.value = Pair(month,year)
    }

    fun setEventList(list : List<BottomDetailDTO>){
        _evenList.value = list
    }
}