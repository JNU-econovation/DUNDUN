package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeGetDTO
import com.project981.dundun.model.repository.MainRepository

class DeleteNoticeUseCase {
    suspend operator fun invoke(
        noticeId : String,
        callback: (Boolean) -> Unit
    ) {
        return MainRepository.deleteNotice(noticeId, callback)
    }
}