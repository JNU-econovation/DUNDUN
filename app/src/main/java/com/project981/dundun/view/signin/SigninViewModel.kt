package com.project981.dundun.view.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.UserSignInUseCase
import kotlinx.coroutines.launch

class SigninViewModel : ViewModel() {
    private val _isLogin = MutableLiveData(false)
    val isLogin: LiveData<Boolean>
        get() = _isLogin
    private val userSignInUseCase = UserSignInUseCase()

    fun submitSignIn(id: String, pw: String, callback: (Result<Boolean>) -> (Unit)) {
        viewModelScope.launch {
            userSignInUseCase(id, pw) {
                it.onSuccess {
                    _isLogin.postValue(true)
                }
                callback(it)
            }
        }
    }
}