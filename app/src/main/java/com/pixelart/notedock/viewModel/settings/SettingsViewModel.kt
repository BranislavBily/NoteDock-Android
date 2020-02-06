package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class SettingsFragmentViewModel(private val auth: FirebaseAuth) : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _loading = MutableLiveData<Boolean>().apply { postValue(false) }
    val loading: LiveData<Boolean> = _loading

    private val _signedOut = MutableLiveData<Event>()
    val signedOut: LiveData<Event> = _signedOut

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun logOut() {
        auth.signOut()
        _signedOut.postValue(Event())
    }
}