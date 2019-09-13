package com.pixelart.notedock.viewModel

import androidx.lifecycle.MutableLiveData

class MainViewModel {
    val userNameLd = MutableLiveData<String>().also {
        it.postValue("Hello world with MVVM!")
    }
}