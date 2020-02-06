package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class ChangePasswordViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}