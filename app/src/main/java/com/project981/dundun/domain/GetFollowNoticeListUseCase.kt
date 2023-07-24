package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeDisplayDTO
import com.project981.dundun.model.repository.MainRepository

class GetFollowNoticeListUseCase {
    suspend operator fun invoke(
        callback:(List<NoticeDisplayDTO>) -> (Unit)
    ) {
        return MainRepository.getFollowerNoticeList(callback)
    }
}