package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel

class EyeViewModel2 : LifecycleViewModel() {
    private val _eyeVisible = MutableLiveData<Boolean>().apply { postValue(false) }
    val eyeVisible: LiveData<Boolean> = _eyeVisible

    private val _eyeOpen = MutableLiveData<Boolean>().apply { postValue(false) }
    val eyeOpen: LiveData<Boolean> = _eyeOpen

    fun changeEyeVisibility(visible: Boolean) {
        _eyeVisible.postValue(visible)
    }

    fun onEyeClick() {
        val eye = eyeOpen.value
        eye?.let {
            _eyeOpen.postValue(!eye)
        }
    }
}
