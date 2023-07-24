package com.project981.dundun.domain

import com.project981.dundun.model.dto.MarkerDTO
import com.project981.dundun.model.repository.MainRepository

class GetUserIsArtistUseCase {
    suspend operator fun invoke(
        callback: (Boolean) -> Unit
    ) {
        return MainRepository.getIsArtist(callback)
    }
}