package com.pixelart.notedock.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.usecase.KoinTestingUseCase

class MainViewModel(koinTestingUseCase: KoinTestingUseCase,
                    private val folderRepository: FolderRepository): ViewModel() {



    private val _userName = MutableLiveData<String>().also {
        it.postValue("Hello world with MVVM!")
    }
    val userName: LiveData<String> = _userName

    private val _koinTest = MutableLiveData<String>().also {
        it.postValue(koinTestingUseCase.testKoin())
    }
    val koinTest: LiveData<String> = _koinTest

    private val _firebaseTest = MutableLiveData<String>().also {
        it.postValue("nic")
        folderRepository.getFolders(EventListener {list, _ ->
            Log.i("ViewController", list.toString())
        })
    }
    val firebaseTest: LiveData<String> = _firebaseTest
}