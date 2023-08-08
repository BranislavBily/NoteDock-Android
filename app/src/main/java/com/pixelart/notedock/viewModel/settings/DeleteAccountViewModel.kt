package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class DeleteAccountViewModel(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _deleteAccount = MutableLiveData<DeleteAccountEvent>()
    val deleteAccount: LiveData<DeleteAccountEvent> = _deleteAccount

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    val password = MutableLiveData<String>()

    val deleteAccountEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(password) {
            postValue(it.isNotEmpty())
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun deleteAccountClick() {
        reAuthenticate()
    }

    private fun reAuthenticate() {
        auth.currentUser?.let { user ->
            val email = user.email
            val password = password.value
            if (email == null || password == null) {
                _deleteAccount.postValue(DeleteAccountEvent.UnknownError())
            } else {
                startStopDisposeBag?.let { bag ->
                    authRepository.reAuthenticateUser(user, email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doOnSubscribe { _loading.postValue(true) }
                        .subscribe({
                            deleteAccount(user)
                        }, { error ->
                            _loading.postValue(false)
                            _deleteAccount.postValue(handleDeleteAccountError(error))
                        }).addTo(bag)
                }
            }
        }
    }

    private fun deleteAccount(user: FirebaseUser) {
        startStopDisposeBag?.let { bag ->
            authRepository.deleteAccount(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doAfterTerminate { _loading.postValue(false) }
                .subscribe({
                    _deleteAccount.postValue(DeleteAccountEvent.Success())
                }, { error ->
                    _deleteAccount.postValue(handleDeleteAccountError(error))
                }).addTo(bag)
        }
    }

    private fun handleDeleteAccountError(throwable: Throwable): DeleteAccountEvent {
        return when (throwable) {
            is FirebaseNetworkException -> DeleteAccountEvent.NetworkError()
            is FirebaseTooManyRequestsException -> DeleteAccountEvent.TooManyRequests()
            is FirebaseAuthInvalidCredentialsException -> DeleteAccountEvent.WrongPassword()
            else -> {
                DeleteAccountEvent.UnknownError()
            }
        }
    }
}

sealed class DeleteAccountEvent : Event() {
    class Success : DeleteAccountEvent()
    class UnknownError : DeleteAccountEvent()
    class NetworkError : DeleteAccountEvent()
    class WrongPassword : DeleteAccountEvent()
    class TooManyRequests : DeleteAccountEvent()
}