package com.pixelart.notedock.viewModel.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import com.pixelart.notedock.domain.repository.InvalidEmailException
import com.pixelart.notedock.viewModel.authentication.LoginEvent.*
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class LoginFragmentViewModel(private val authRepository: AuthRepository,
                             private val auth: FirebaseAuth): LifecycleViewModel() {

    private val _loginCompleted = MutableLiveData<LoginEvent>()
    val loginCompleted: LiveData<LoginEvent> = _loginCompleted

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    private val _sendEmail = MutableLiveData<SendEmailEvent>()
    val sendEmail: LiveData<SendEmailEvent> = _sendEmail

    private val _forgotPassword = MutableLiveData<ButtonPressedEvent>()
    val forgotPassword: LiveData<ButtonPressedEvent> = _forgotPassword

    private val _createAccount = MutableLiveData<CreateAccountEvent>()
    val createAccount: LiveData<CreateAccountEvent> = _createAccount

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
                        isUserVerified()
                    }, {
                        _loginCompleted.postValue(handleLoginError(it))
                    }).addTo(bag)
            }
        }
    }

    fun sendVerificationEmail() {
        startStopDisposeBag?.let {bag ->
            auth.currentUser?.let { user ->
                authRepository.sendVerificationEmail(user)
                    .subscribeOn(Schedulers.io())
                    .doAfterTerminate { _loading.postValue( false) }
                    .subscribe({
                        _sendEmail.postValue(SendEmailEvent.Success())
                    }, { error ->
                        Log.e("SendEmail", error.printStackTrace().toString())
                        _sendEmail.postValue(SendEmailEvent.UnknownError())
                    }).addTo(bag)
            }
        }
    }

    private fun isUserVerified() {
        auth.currentUser?.let { user ->
            if (user.isEmailVerified) {
                _loginCompleted.postValue(Success())
            } else {
                _loginCompleted.postValue(UserEmailNotVerified())
            }
        }
    }

    fun forgotPassword() {
        _forgotPassword.postValue(ButtonPressedEvent.Pressed())
    }

    fun createAccount() {
        _createAccount.postValue(CreateAccountEvent.Pressed())
    }

    private fun handleLoginError(throwable: Throwable?): LoginEvent {
        return when (throwable) {
            is InvalidEmailException -> InvalidEmail()
            is FirebaseAuthInvalidUserException -> BadCredentials()
            is FirebaseAuthInvalidCredentialsException -> BadCredentials()
            is FirebaseTooManyRequestsException -> TooManyRequests()
            is FirebaseException -> NetworkError()
            else -> {
                Log.e("Login", "${throwable?.message}", throwable)
                UnknownError()
            }
        }
    }
}

sealed class SendEmailEvent : Event() {
    class Success : SendEmailEvent()
    class UnknownError : SendEmailEvent()
    class NetworkError: SendEmailEvent()
}

sealed class LoginEvent : Event() {
    class Success : LoginEvent()
    class InvalidEmail : LoginEvent()
    class BadCredentials : LoginEvent()
    class NetworkError : LoginEvent()
    class UserEmailNotVerified : LoginEvent()
    class UnknownError : LoginEvent()
    class TooManyRequests: LoginEvent()
}

sealed class CreateAccountEvent: Event() {
    class Pressed: CreateAccountEvent()
}