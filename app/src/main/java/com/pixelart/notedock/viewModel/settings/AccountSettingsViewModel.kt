package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.disposables.CompositeDisposable

class AccountSettingsViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _updateDisplayName = MutableLiveData<UpdateDisplayNameEvent>()
    val updateDisplayName: LiveData<UpdateDisplayNameEvent> = _updateDisplayName

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser> = _user

    override fun onStartStopObserve(disposeBag: CompositeDisposable) {
        super.onStartStopObserve(disposeBag)

        auth.currentUser?.let { user ->
            _user.postValue(user)
        }
    }

    fun updateDisplayName(displayName: String?) {
        _loading.postValue(true)
        auth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    _loading.postValue(false)
                    _updateDisplayName.postValue(UpdateDisplayNameEvent.Success())
                }
                .addOnFailureListener { error ->
                    _updateDisplayName.postValue(handleError(error))
                    _loading.postValue(false)
                }
        }
    }

    private fun handleError(throwable: Throwable): UpdateDisplayNameEvent {
        return when (throwable) {
            is FirebaseNetworkException -> UpdateDisplayNameEvent.NetworkError()
            else -> {
                Crashlytics.logException(throwable)
                UpdateDisplayNameEvent.UnknownError()
            }
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}

sealed class UpdateDisplayNameEvent : Event() {
    class Success : UpdateDisplayNameEvent()
    class UnknownError : UpdateDisplayNameEvent()
    class NetworkError : UpdateDisplayNameEvent()
}