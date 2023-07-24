package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class ChangeArtistFollowUseCase {
    suspend operator fun invoke(
        artistId: String,
        isFollow: Boolean,
        callback: (Boolean) -> (Unit)
    ) {
        return MainRepository.changeFollowArtist(artistId, isFollow, callback)
    }
}