package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class UserSignUpUseCase {
    operator fun invoke(
        email: String,
        pw: String,
        name: String,
        isArtist: Boolean,
        callback: (Result<Boolean>) -> (Unit)
    ) {
        return MainRepository.createUser(email, pw, name, isArtist, callback)
    }
}