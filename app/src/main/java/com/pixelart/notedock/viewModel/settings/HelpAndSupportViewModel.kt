package com.pixelart.notedock.viewModel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent

class HelpAndSupportViewModel : LifecycleViewModel() {

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }
}