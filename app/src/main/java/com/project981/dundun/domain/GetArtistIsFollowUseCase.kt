package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class GetArtistIsFollowUseCase {
    suspend operator fun invoke(
        artistId: String,
        callback:(Boolean) -> (Unit)
    ) {
        return MainRepository.getArtistIsFollow(artistId, callback)
    }
}