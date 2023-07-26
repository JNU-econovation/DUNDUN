package com.project981.dundun.domain

import android.graphics.Bitmap
import com.project981.dundun.model.repository.MainRepository

class UpdateArtistInfoUseCase {
    suspend operator fun invoke(
        name: String,
        noticeId: Bitmap?,
        description: String,
        artistId: String,
        callback: (Boolean) -> Unit
    ) {
        return MainRepository.updateArtistInfo(name, noticeId, description, artistId ,callback)
    }
}