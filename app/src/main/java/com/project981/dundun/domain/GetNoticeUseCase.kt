package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeChangeDTO
import com.project981.dundun.model.dto.NoticeGetDTO
import com.project981.dundun.model.repository.MainRepository

class GetNoticeUseCase {
    suspend operator fun invoke(
        noticeId : String,
        callback: (NoticeGetDTO) -> Unit
    ) {
        return MainRepository.getNotice(noticeId, callback)
    }
}