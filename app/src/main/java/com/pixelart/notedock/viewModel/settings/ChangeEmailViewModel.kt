package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ChangeEmailViewModel(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _changeEmail = MutableLiveData<ChangeEmailEvent>()
    val changeEmail: LiveData<ChangeEmailEvent> = _changeEmail

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val changeEmailEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(it.isNotEmpty() && password.value?.isNotEmpty() == true)
        }
        addSource(password) {
            postValue(it.isNotEmpty() && email.value?.isNotEmpty() == true)
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun changeEmailClick() {
        val email = email.value
        val password = password.value

        if (email != null && password != null) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _changeEmail.postValue(ChangeEmailEvent.InvalidEmail())
            } else {
                reAuthenticate(email, password)
            }
        } else {
            _changeEmail.postValue(ChangeEmailEvent.UnknownError())
        }
    }

    private fun reAuthenticate(email: String, password: String) {
        auth.currentUser?.let { user ->
            user.email?.let { currentEmail ->
                if (currentEmail == email) {
                    _changeEmail.postValue(ChangeEmailEvent.UseNewEmail())
                } else {
                    startStopDisposeBag?.let { bag ->
                        authRepository.reAuthenticateUser(user, currentEmail, password)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .doOnSubscribe { _loading.postValue(true) }
                            .subscribe({
                                updateEmail(user, email)
                            }, { error ->
                                _loading.postValue(false)
                                _changeEmail.postValue(handleError(error))
                            }).addTo(bag)
                    }
                }
            }
        }
    }

    private fun updateEmail(user: FirebaseUser, newEmail: String) {
        startStopDisposeBag?.let { bag ->
            authRepository.updateEmail(user, newEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doAfterTerminate { _loading.postValue(false) }
                .subscribe({
                    _changeEmail.postValue(ChangeEmailEvent.Success())
                }, { error ->
                    _changeEmail.postValue(handleError(error))
                }).addTo(bag)
        }
    }

    private fun handleError(throwable: Throwable): ChangeEmailEvent {
        return when (throwable) {
            is FirebaseNetworkException -> ChangeEmailEvent.NetworkError()
            is FirebaseAuthEmailException -> ChangeEmailEvent.EmailAlreadyUsed()
            is FirebaseAuthInvalidCredentialsException -> ChangeEmailEvent.InvalidPassword()
            is FirebaseAuthInvalidUserException -> {
                //This happens if user wants to change email and cached FirebaseUser has old email address
                auth.currentUser?.reload()?.addOnFailureListener { Crashlytics.logException(it) }
                ChangeEmailEvent.TryAgainError()
            }
            else -> {
                Crashlytics.logException(throwable)
                ChangeEmailEvent.UnknownError()
            }
        }
    }
}

sealed class ChangeEmailEvent : Event() {
    class Success : ChangeEmailEvent()
    class NetworkError : ChangeEmailEvent()
    class InvalidEmail : ChangeEmailEvent()
    class UseNewEmail : ChangeEmailEvent()
    class EmailAlreadyUsed : ChangeEmailEvent()
    class InvalidPassword : ChangeEmailEvent()
    class TryAgainError : ChangeEmailEvent()
    class UnknownError : ChangeEmailEvent()
}