package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class ChangeNoticeLikeUseCase {
    suspend operator fun invoke(
        noticeId: String,
        isLike: Boolean,
        callback:(Boolean) -> (Unit)
    ) {
        return MainRepository.changeNoticeLike(noticeId, isLike, callback)
    }
}