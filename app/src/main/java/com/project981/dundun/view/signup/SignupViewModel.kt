package com.project981.dundun.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.IdDuplicateCheckUseCase
import com.project981.dundun.domain.UserSignUpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val nameRegex: Regex = "^[가-힣a-zA-Z0-9]+".toRegex()
    private val pwRegex: Regex = "^[a-zA-Z0-9]+".toRegex()
    private val emailRegex: Regex = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+".toRegex()
    val idDuplicateCheckUseCase = IdDuplicateCheckUseCase()
    val userSignUpUseCase = UserSignUpUseCase()
    //TODO : Inject


    private val _idState = MutableLiveData(IdStateEnum.NONE)
    val idState: LiveData<IdStateEnum>
        get() = _idState
    private var idJob: Job? = null
    private val _nameState = MutableLiveData(NameStateEnum.NONE)
    val nameState: LiveData<NameStateEnum>
        get() = _nameState
    private var nameJob: Job? = null
    private val _pwState = MutableLiveData(PwStateEnum.NONE)
    val pwState: LiveData<PwStateEnum>
        get() = _pwState
    private var pwJob: Job? = null
    private val _pwRepeatState = MutableLiveData(PwRepeatStateEnum.NONE)
    val pwRepeatState: LiveData<PwRepeatStateEnum>
        get() = _pwRepeatState
    private var pwRepeatJob: Job? = null

    fun changeId(email: String) {
        idJob?.cancel()
        _idState.value = IdStateEnum.NONE
        idJob = viewModelScope.launch(Dispatchers.IO) {
            delay(DELAY_MS)
            if (!emailRegex.matches(email)) {
                _idState.postValue(IdStateEnum.TYPE)
                return@launch
            }
            idDuplicateCheckUseCase(email) { result ->
                result.onSuccess {
                    if (it) {
                        _idState.postValue(IdStateEnum.DUPLICATE)
                    } else {
                        _idState.postValue(IdStateEnum.CORRECT)
                    }
                }.onFailure {
                    _idState.postValue(IdStateEnum.ERROR)
                }
            }
        }
    }

    fun changeName(name: String) {
        nameJob?.cancel()
        _nameState.value = NameStateEnum.NONE
        nameJob = viewModelScope.launch(Dispatchers.IO) {
            delay(DELAY_MS)
            if (name.length > NAME_LEN_MAX || name.isEmpty()) {
                _nameState.postValue(NameStateEnum.LENGTH)
                return@launch
            }

            if (!nameRegex.matches(name)) {
                _nameState.postValue(NameStateEnum.TYPE)
                return@launch
            }

            _nameState.postValue(NameStateEnum.CORRECT)
        }
    }

    fun changePw(pw: String) {
        pwJob?.cancel()
        _pwState.value = PwStateEnum.NONE
        pwJob = viewModelScope.launch(Dispatchers.IO) {
            delay(DELAY_MS)
            if (!pwRegex.matches(pw)) {
                _pwState.postValue(PwStateEnum.TYPE)
                return@launch
            }

            if (pw.length in PW_LEN_MIN..PW_LEN_MAX) {
                _pwState.postValue(PwStateEnum.CORRECT)
            } else {
                _pwState.postValue(PwStateEnum.LENGTH)
            }

        }

    }

    fun changePwRepeat(pwRepeat: String, checking: () -> (Boolean)) {
        pwRepeatJob?.cancel()
        _pwRepeatState.value = PwRepeatStateEnum.NONE
        pwRepeatJob = viewModelScope.launch(Dispatchers.IO) {
            delay(DELAY_MS)
            if (checking()) {
                _pwRepeatState.postValue(PwRepeatStateEnum.CORRECT)
            } else {
                _pwRepeatState.postValue(PwRepeatStateEnum.DIFF)
            }
        }
    }


    fun submitSignup(email: String, pw: String, name: String, viewCallback: (Boolean) -> (Unit)) {
        viewModelScope.launch(Dispatchers.IO) {
            userSignUpUseCase(email, pw, name) {
                it.onSuccess {
                    viewCallback(true)
                }.onFailure {
                    viewCallback(false)
                }
            }
        }
    }

    companion object {
        const val DELAY_MS = 1000L
        const val NAME_LEN_MAX = 8
        const val PW_LEN_MAX = 20
        const val PW_LEN_MIN = 8
    }

}