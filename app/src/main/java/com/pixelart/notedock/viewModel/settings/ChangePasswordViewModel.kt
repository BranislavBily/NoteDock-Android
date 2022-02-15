package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ChangePasswordViewModel(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _changePassword = MutableLiveData<ChangePasswordEvent>()
    val changePassword: LiveData<ChangePasswordEvent> = _changePassword

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val currentPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val confirmNewPassword = MutableLiveData<String>()

    val savePasswordEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(currentPassword) {
            postValue(it.isNotEmpty() && newPassword.value?.isNotEmpty() == true && confirmNewPassword.value?.isNotEmpty() == true)
        }

        addSource(newPassword) {
            postValue(it.isNotEmpty() && currentPassword.value?.isNotEmpty() == true && confirmNewPassword.value?.isNotEmpty() == true)
        }

        addSource(confirmNewPassword) {
            postValue(it.isNotEmpty() && currentPassword.value?.isNotEmpty() == true && newPassword.value?.isNotEmpty() == true)
        }
    }

    fun saveNewPassword() {
        //If all fields are filled
        if (savePasswordEnabled.value == false) {
            _changePassword.postValue(ChangePasswordEvent.FillAllFields())
            //Posting false
            _loading.postValue(false)
        } else {
            // If values of newPassword and confirm password are null for some reason
            if (currentPassword.value != null && newPassword.value != null && confirmNewPassword.value != null) {
                //If passwords do not match
                if (!newPassword.value.equals(confirmNewPassword.value)) {
                    _changePassword.postValue(ChangePasswordEvent.PasswordsDoNotMatch())
                } else {
                    reAuthenticateUser()
                }
            } else {
                _changePassword.postValue(ChangePasswordEvent.UnknownError())
            }
        }
    }

    private fun reAuthenticateUser() {
        auth.currentUser?.let { user ->
            val email = user.email
            if (email != null) {
                startStopDisposeBag?.let { bag ->
                    //I checked if its null already please Kotlin
                    authRepository.reAuthenticateUser(user, email, currentPassword.value!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doOnSubscribe { _loading.postValue(true) }
                        .subscribe({
                            changePassword(user, currentPassword.value!!, newPassword.value!!)
                        }, { error ->
                            _loading.postValue(false)
                            _changePassword.postValue(handleChangePasswordError(error))
                        }).addTo(bag)
                }
            }
        } ?: run {
            _changePassword.postValue(ChangePasswordEvent.UserNotFound())
        }
    }

    private fun changePassword(user: FirebaseUser, currentPassword: String, newPassword: String) {
        if (currentPassword == newPassword) {
            _loading.postValue(false)
            _changePassword.postValue(ChangePasswordEvent.NewPasswordCannotBeCurrent())
        } else {
            startStopDisposeBag?.let { bag ->
                authRepository.changePassword(user, newPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doAfterTerminate { _loading.postValue(false) }
                    .subscribe({
                        _changePassword.postValue(ChangePasswordEvent.Success())
                    }, { error ->
                        _changePassword.postValue(handleChangePasswordError(error))
                    }).addTo(bag)
            }
        }
    }

    private fun handleChangePasswordError(throwable: Throwable): ChangePasswordEvent {
        return when (throwable) {
            is FirebaseAuthWeakPasswordException -> ChangePasswordEvent.WeakPassword()
            is FirebaseNetworkException -> ChangePasswordEvent.NetworkError()
            is FirebaseTooManyRequestsException -> ChangePasswordEvent.TooManyRequests()
            is FirebaseAuthInvalidCredentialsException -> ChangePasswordEvent.WrongPassword()
            else -> {
                Crashlytics.logException(throwable)
                ChangePasswordEvent.UnknownError()
            }
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}

sealed class ChangePasswordEvent : Event() {
    class Success : ChangePasswordEvent()
    class NetworkError : ChangePasswordEvent()
    class UserNotFound : ChangePasswordEvent()
    class PasswordsDoNotMatch : ChangePasswordEvent()
    class WeakPassword : ChangePasswordEvent()
    class WrongPassword : ChangePasswordEvent()
    class NewPasswordCannotBeCurrent : ChangePasswordEvent()
    class FillAllFields : ChangePasswordEvent()
    class TooManyRequests : ChangePasswordEvent()
    class UnknownError : ChangePasswordEvent()
}