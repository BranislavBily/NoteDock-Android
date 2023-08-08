package com.pixelart.notedock.viewModel.authentication

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ResetPasswordFragmentViewModel(private val authRepository: AuthRepository) :
    LifecycleViewModel() {

    val email = MutableLiveData<String>()

    private val _backToLogin = MutableLiveData<ButtonPressedEvent>()
    val backToLogin: LiveData<ButtonPressedEvent> = _backToLogin

    private val _recoverAccountPressed = MutableLiveData<ButtonPressedEvent>()
    val recoverAccountPressed: LiveData<ButtonPressedEvent> = _recoverAccountPressed

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    private val _recoverAccount = MutableLiveData<RecoverAccountEvent>()
    val recoverAccount: LiveData<RecoverAccountEvent> = _recoverAccount

    val recoverEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(Patterns.EMAIL_ADDRESS.matcher(it).matches())
        }
    }

    fun recoverAccountPressed() {
        _recoverAccountPressed.postValue(ButtonPressedEvent.Pressed())
        recoverAccount()
    }

    private fun recoverAccount() {
        val emailValue = email.value
        emailValue?.let { email ->
            startStopDisposeBag?.let { bag ->
                authRepository.sendPasswordResetEmail(email)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { _loading.postValue(true) }
                    .doOnTerminate { _loading.postValue(false) }
                    .subscribe({
                        _recoverAccount.postValue(RecoverAccountEvent.Success())
                    }, { error ->
                        val errorEvent = handleRecoverError(error)
                        _recoverAccount.postValue(RecoverAccountEvent.Error(errorEvent))
                    }).addTo(bag)
            }
        }
    }

    fun goBackToLogin() {
        _backToLogin.postValue(ButtonPressedEvent.Pressed())
    }

    private fun handleRecoverError(throwable: Throwable?): RecoverAccountEventError {
        return when (throwable) {
            is FirebaseAuthInvalidUserException -> RecoverAccountEventError.InvalidEmail()
            is FirebaseNetworkException -> RecoverAccountEventError.NetworkError()
            is FirebaseTooManyRequestsException -> RecoverAccountEventError.TooManyRequests()
            else -> {
                RecoverAccountEventError.UnknownError()
            }
        }
    }
}

sealed class RecoverAccountEvent : Event() {
    class Success : RecoverAccountEvent()
    class Error(val error: RecoverAccountEventError) : RecoverAccountEvent()
}

sealed class RecoverAccountEventError : Event() {
    class InvalidEmail : RecoverAccountEventError()
    class NetworkError : RecoverAccountEventError()
    class UnknownError : RecoverAccountEventError()
    class TooManyRequests : RecoverAccountEventError()
}
