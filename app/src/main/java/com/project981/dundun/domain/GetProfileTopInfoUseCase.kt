package com.project981.dundun.domain

import com.project981.dundun.model.dto.ProfileTopDTO
import com.project981.dundun.model.repository.MainRepository

class GetProfileTopInfoUseCase {
    suspend operator fun invoke(
        artistId : String,
        callback: (ProfileTopDTO) -> Unit
    ) {
        return MainRepository.getArtistTopInfo(artistId, callback)
    }
}