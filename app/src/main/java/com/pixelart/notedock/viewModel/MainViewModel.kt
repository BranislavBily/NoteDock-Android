package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixelart.notedock.domain.usecase.KoinTestingUseCase

class MainViewModel(koinTestingUseCase: KoinTestingUseCase): ViewModel() {



    private val _userName = MutableLiveData<String>().also {
        it.postValue("Hello world with MVVM!")
    }
    val userName: LiveData<String> = _userName

    private val _koinTest = MutableLiveData<String>().also {
        it.postValue(koinTestingUseCase.testKoin())
    }
    val koinTest: LiveData<String> = _koinTest
}