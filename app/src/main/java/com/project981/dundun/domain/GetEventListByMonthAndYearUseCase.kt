package com.project981.dundun.domain

import com.project981.dundun.model.dto.BottomDetailDTO
import com.project981.dundun.model.repository.MainRepository

class GetEventListByMonthAndYearUseCase {
    suspend operator fun invoke(
        month: Int,
        year: Int,
        callback:( List<List<BottomDetailDTO>>) -> (Unit)
    ) {
        return MainRepository.getFollowerNoticeIdListWithMonthYear(month, year, callback)
    }
}