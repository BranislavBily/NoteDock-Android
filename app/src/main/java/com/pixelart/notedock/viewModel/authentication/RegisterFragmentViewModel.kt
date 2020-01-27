package com.pixelart.notedock.viewModel.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class RegisterFragmentViewModel(private val authRepository: AuthRepository,
                                private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _register = MutableLiveData<RegisterEvent>()
    val register: LiveData<RegisterEvent> = _register

    private val _registerButtonPressed = MutableLiveData<ButtonPressedEvent>()
    val registerButtonPressedEvent: LiveData<ButtonPressedEvent> = _registerButtonPressed

    private val _alreadyHaveAccount = MutableLiveData<ButtonPressedEvent>()
    val alreadyHaveAccount: LiveData<ButtonPressedEvent> = _alreadyHaveAccount

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    val registerEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(it.isNotEmpty() && password.value?.isNotEmpty() == true && confirmPassword.value?.isNotEmpty() == true)
        }
        addSource(password) {
            postValue(it.isNotEmpty() && email.value?.isNotEmpty() == true && confirmPassword.value?.isNotEmpty() == true)
        }
        addSource(confirmPassword) {
            postValue(it.isNotEmpty() && email.value?.isNotEmpty() == true && password.value?.isNotEmpty() == true)
        }
    }

    fun registerButtonPressed() {
        _registerButtonPressed.postValue(ButtonPressedEvent.Pressed())
        register()
    }

    private fun register() {
        val email = email.value
        val password = password.value
        val confirmPassword = confirmPassword.value

        if (email == null || password == null || confirmPassword == null) {
            return
        } else if (!password.contentEquals(confirmPassword)) {
            _register.postValue(RegisterEvent.Error(RegisterEventError.PasswordsDoNotMatch()))
        } else {
            startStopDisposeBag?.let { bag ->
                authRepository.register(email, password)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { _loading.postValue(true) }
                    .subscribe({
                        sendVerificationEmail()
                        //Firebase logs in after registration, we do not want that
                        auth.signOut()
                    }, { error ->
                        val eventError = handleRegisterError(error)
                        _register.postValue(RegisterEvent.Error(eventError))
                    }).addTo(bag)
            }
        }
    }

    private fun sendVerificationEmail() {
        startStopDisposeBag?.let {bag ->
            auth.currentUser?.let { user ->
                authRepository.sendVerificationEmail(user)
                    .subscribeOn(Schedulers.io())
                    .doAfterTerminate { _loading.postValue( false) }
                    .subscribe({
                        _register.postValue(RegisterEvent.Success())
                    }, { error ->
                        val eventError = handleRegisterError(error)
                        _register.postValue(RegisterEvent.Error(eventError))
                    }).addTo(bag)
            }
        }
    }

    fun alreadyHaveAccount() {
        _alreadyHaveAccount.postValue(ButtonPressedEvent.Pressed())
    }

    private fun handleRegisterError(throwable: Throwable?): RegisterEventError {
        return when (throwable) {
            is FirebaseAuthWeakPasswordException -> RegisterEventError.WeakPassword()
            is FirebaseAuthInvalidCredentialsException -> RegisterEventError.InvalidEmail()
            is FirebaseAuthUserCollisionException -> RegisterEventError.EmailAlreadyUsed()
            is FirebaseException -> RegisterEventError.NetworkError()
            else -> {
                Log.e("Register", "${throwable?.message}", throwable)
                RegisterEventError.UnknownError()
            }
        }
    }
}

sealed class ButtonPressedEvent : Event() {
    class Pressed : ButtonPressedEvent()
}

sealed class RegisterEvent : Event() {
    class Success : RegisterEvent()
    class Error(val error: RegisterEventError) : RegisterEvent()
}

sealed class RegisterEventError : Event() {
    class EmailAlreadyUsed : RegisterEventError()
    class PasswordsDoNotMatch : RegisterEventError()
    class NetworkError : RegisterEventError()
    class InvalidEmail : RegisterEventError()
    class WeakPassword : RegisterEventError()
    class UnknownError : RegisterEventError()
}