package com.pixelart.notedock.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixelart.notedock.domain.usecase.KoinTestingUseCase

class MainViewModel(koinTestingUseCase: KoinTestingUseCase): ViewModel() {



    val userNameLd = MutableLiveData<String>().also {
        it.postValue("Hello world with MVVM!")
    }

    val koinTestLd = MutableLiveData<String>().also {
        it.postValue(koinTestingUseCase.testKoin())
    }
}