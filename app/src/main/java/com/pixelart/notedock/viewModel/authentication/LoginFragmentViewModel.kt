package com.pixelart.notedock.viewModel.authentication

import androidx.lifecycle.LiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel

class LoginFragmentViewModel: LifecycleViewModel() {

    private val _loginEvent = SingleLiveEvent<LoginEvent>()
    val loginEvent: LiveData<LoginEvent> = _loginEvent

    fun login() {
        _loginEvent.postValue(LoginEvent.Success)
    }
}

sealed class LoginEvent {
    object Success: LoginEvent()
    object Error: LoginEvent()
}