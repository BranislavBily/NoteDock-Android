package com.pixelart.notedock.viewModel.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class ChangeEmailViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    val changeEmailEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(it.isNotEmpty() &&  password.value?.isNotEmpty() == true)
        }
        addSource(password) {
            postValue(it.isNotEmpty() &&  email.value?.isNotEmpty() == true)
        }
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun changeEmailClick() {
        Log.i("Change email", "Clicked")
    }
}