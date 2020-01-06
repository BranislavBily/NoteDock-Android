package com.pixelart.notedock.viewModel.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.AuthRepository
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class RegisterFragmentViewModel(private val authRepository: AuthRepository): LifecycleViewModel() {

    private val _register = SingleLiveEvent<RegisterEvent>()
    val register: LiveData<RegisterEvent> = _register

    fun register(email: String, password: String) {
        startStopDisposeBag?.let { bag ->
            authRepository.register(email, password)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _register.postValue(RegisterEvent.Success())
                }, { error ->
                    val eventError = handleRegisterError(error)
                    _register.postValue(RegisterEvent.Error(eventError))
                }).addTo(bag)
        }
    }

    private fun handleRegisterError(throwable: Throwable?): RegisterEventError {
        return when(throwable) {
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

sealed class RegisterEvent: Event() {
    class Success: RegisterEvent()
    class Error(val error: RegisterEventError): RegisterEvent()
}

sealed class RegisterEventError: Event() {
    class EmailAlreadyUsed: RegisterEventError()
    class NetworkError: RegisterEventError()
    class InvalidEmail: RegisterEventError()
    class WeakPassword: RegisterEventError()
    class UnknownError: RegisterEventError()
}