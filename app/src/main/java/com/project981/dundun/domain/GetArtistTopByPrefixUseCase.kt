package com.project981.dundun.domain

import com.project981.dundun.model.dto.ProfileTopDTO
import com.project981.dundun.model.repository.MainRepository

class GetArtistTopByPrefixUseCase {
    suspend operator fun invoke(
        prefix: String,
        callback: (List<ProfileTopDTO>) -> (Unit)
    ) {
        return MainRepository.getArtistTopByPrefix(prefix, callback)
    }
}