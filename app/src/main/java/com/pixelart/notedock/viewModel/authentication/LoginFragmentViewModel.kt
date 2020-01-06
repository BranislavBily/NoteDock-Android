package com.pixelart.notedock.viewModel.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import com.pixelart.notedock.domain.repository.InvalidEmailException
import com.pixelart.notedock.viewModel.authentication.LoginEvent.*
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class LoginFragmentViewModel(private val authRepository: AuthRepository): LifecycleViewModel() {

    private val _loginCompleted = SingleLiveEvent<LoginEvent>()
    val loginCompleted: LiveData<LoginEvent> = _loginCompleted

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    private val _forgotPassword = SingleLiveEvent<ForgotPasswordEvent>()
    val forgotPassword: LiveData<ForgotPasswordEvent> = _forgotPassword

    val loginEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(it.isNotEmpty() && password.value?.isNotEmpty() == true)
        }
        addSource(password) {
            postValue(it.isNotEmpty() && email.value?.isNotEmpty() == true)
        }
    }

    fun login() {
        val email = email.value
        val password = password.value

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            startStopDisposeBag?.let { bag ->
                (authRepository.login(email, password))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSubscribe { _loading.postValue(true) }
                    .doAfterTerminate { _loading.postValue(false) }
                    .subscribe({
                        _loginCompleted.postValue(Success())
                    }, {
                        _loginCompleted.postValue(handleLoginError(it))
                    }).addTo(bag)
            }
        }
    }

    fun forgotPassword() {
        _forgotPassword.postValue(ForgotPasswordEvent.Pressed())
    }

    fun createAccount() {
        Log.i("LoginViewModel", "Create account pressed")
    }

    private fun handleLoginError(throwable: Throwable?): LoginEvent {
        return when (throwable) {
            is InvalidEmailException -> InvalidEmail()
            is FirebaseException -> NetworkError()
            is FirebaseAuthInvalidCredentialsException -> BadCredentials()
            else -> {
                Log.e("Login", "${throwable?.message}", throwable)
                UnknownError()
            }
        }
    }
}

sealed class LoginEvent : Event() {
    class Success : LoginEvent()
    class InvalidEmail : LoginEvent()
    class BadCredentials : LoginEvent()
    class NetworkError : LoginEvent()
    class UnknownError : LoginEvent()
}

sealed class ForgotPasswordEvent: Event() {
    class Pressed: ForgotPasswordEvent()
}