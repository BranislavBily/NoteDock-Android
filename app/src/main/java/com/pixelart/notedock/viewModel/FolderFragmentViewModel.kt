package com.pixelart.notedock.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FolderFragmentViewModel: ViewModel() {

    private val _buttonClicked = MutableLiveData<Boolean>()
    val buttonClicked: LiveData<Boolean> = _buttonClicked

    fun onButtonClicked() {
        _buttonClicked.postValue(true)
    }

}