package com.project981.dundun.view.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.CreateNoticeUseCase
import com.project981.dundun.domain.GetNoticeUseCase
import com.project981.dundun.domain.GetProfileTopInfoUseCase
import com.project981.dundun.domain.UpdateNoticeUseCase
import com.project981.dundun.model.dto.NoticeChangeDTO
import com.project981.dundun.model.dto.NoticeCreateDTO
import com.project981.dundun.model.dto.NoticeGetDTO
import com.project981.dundun.model.dto.ProfileTopDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WriteNoticeViewModel : ViewModel() {
    private val writeUseCase = CreateNoticeUseCase()
    private val editUseCase = UpdateNoticeUseCase()
    private val noticeUseCase = GetNoticeUseCase()
    private val profileTopUseCase = GetProfileTopInfoUseCase()

    fun editNotice(info: NoticeChangeDTO, noticeId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            editUseCase(info, noticeId, callback)
        }
    }

    fun createNotice(info: NoticeCreateDTO, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            writeUseCase(info, callback)
        }
    }

    fun getNotice(noticeId: String, callback: (NoticeGetDTO) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            noticeUseCase(noticeId, callback)
        }
    }

    fun getProfileTop(artistId: String, callback: (ProfileTopDTO) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            profileTopUseCase(artistId, callback)
        }
    }
}