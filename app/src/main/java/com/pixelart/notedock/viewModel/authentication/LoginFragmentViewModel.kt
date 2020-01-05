package com.pixelart.notedock.viewModel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.AuthRepository

class LoginFragmentViewModel(private val authRepository: AuthRepository): LifecycleViewModel() {

    private val _loginButtonPressed = SingleLiveEvent<LoginButtonEvent>()
    val loginButtonEvent: LiveData<LoginButtonEvent> = _loginButtonPressed

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val loginEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(it.isNotEmpty() && password.value?.isNotEmpty() == true)
        }
        addSource(password) {
            postValue(it.isNotEmpty() && email.value?.isNotEmpty() == true)
        }
    }

    fun loginButtonPressed() {
        _loginButtonPressed.postValue(LoginButtonEvent.Pressed)
    }

    fun login(email: String, password: String) {
        startStopDisposeBag?.let { bag ->

        }
    }
}

sealed class LoginButtonEvent {
    object Pressed: LoginButtonEvent()
}