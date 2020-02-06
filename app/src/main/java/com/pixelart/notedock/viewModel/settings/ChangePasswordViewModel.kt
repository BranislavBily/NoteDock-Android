package com.pixelart.notedock.viewModel.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class ChangePasswordViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _changePassword = MutableLiveData<ChangePasswordEvent>()
    val changePassword: LiveData<ChangePasswordEvent> = _changePassword

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
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
        if(savePasswordEnabled.value == true) {
            Log.i("Saveing", "HAhahaha")
        } else {
            _changePassword.postValue(ChangePasswordEvent.FillAllFields())
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}

sealed class ChangePasswordEvent: Event() {
    class Success: ChangePasswordEvent()
    class NetworkError: ChangePasswordEvent()
    class UserNotFound: ChangePasswordEvent()
    class PasswordsDoNotMatch: ChangePasswordEvent()
    class WeakPassword: ChangePasswordEvent()
    class WrongPasword: ChangePasswordEvent()
    class NewPasswordCannotBeOld: ChangePasswordEvent()
    class FillAllFields: ChangePasswordEvent()
}