package com.project981.dundun.domain

import com.project981.dundun.model.dto.NoticeDisplayDTO
import com.project981.dundun.model.repository.MainRepository

class GetArtistNoticeListUseCase {
    suspend operator fun invoke(
        artistId: String,
        callback:( List<NoticeDisplayDTO>) -> (Unit)
    ) {
        return MainRepository.getArtistNoticeList(artistId, callback)
    }
}