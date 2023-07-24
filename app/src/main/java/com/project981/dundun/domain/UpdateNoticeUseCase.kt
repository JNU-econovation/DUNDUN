package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeChangeDTO
import com.project981.dundun.model.repository.MainRepository

class UpdateNoticeUseCase {
    suspend operator fun invoke(
        info: NoticeChangeDTO,
        noticeId: String,
        callback: (Boolean) -> (Unit)
    ) {
        return MainRepository.updateNotice(info, noticeId, callback)
    }
}