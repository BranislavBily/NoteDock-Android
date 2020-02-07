package com.pixelart.notedock.viewModel.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class DeleteAccountViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _loading = MutableLiveData<Boolean>()
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

    fun deleteAccount() {
        Log.i("Delete", password.value)
    }
}