package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class IdDuplicateCheckUseCase {
    suspend operator fun invoke(
        email: String,
        callback:( Result<Boolean>) -> (Unit)
    ) {
        return MainRepository.checkIdDuplicate(email, callback)
    }
}