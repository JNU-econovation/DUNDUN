package com.project981.dundun.domain

import com.project981.dundun.model.repository.MainRepository

class UserSignInUseCase {
    operator fun invoke(
        email: String,
        pw: String,
        callback: (Result<Boolean>) -> (Unit)
    ) {
        return MainRepository.checkSignIn(email, pw, callback)
    }
}