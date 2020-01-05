package com.pixelart.notedock.viewModel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel

class LoginFragmentViewModel: LifecycleViewModel() {

    private val _loginEvent = SingleLiveEvent<LoginEvent>()
    val loginEvent: LiveData<LoginEvent> = _loginEvent

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
        _loginEvent.postValue(LoginEvent.Success)
    }



}

sealed class LoginEvent {
    object Success: LoginEvent()
    object Error: LoginEvent()
}