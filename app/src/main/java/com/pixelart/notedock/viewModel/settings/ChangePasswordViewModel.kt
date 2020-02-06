package com.pixelart.notedock.viewModel.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class ChangePasswordViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

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
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}