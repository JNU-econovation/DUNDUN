package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeCreateDTO
import com.project981.dundun.model.repository.MainRepository

class CreateNoticeUseCase {
    suspend operator fun invoke(
        info: NoticeCreateDTO,
        callback: (Boolean) -> (Unit)
    ) {
        return MainRepository.createNotice(info, callback)
    }
}
