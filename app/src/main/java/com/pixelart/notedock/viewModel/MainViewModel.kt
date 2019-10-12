package com.pixelart.notedock.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.module.FolderModule

class MainViewModel(private val folderRepository: FolderRepository): ViewModel() {

    private val _firebaseTest = MutableLiveData<ArrayList<FolderModule>>().also { liveData ->
        folderRepository.getFolders(EventListener {list, _ ->
            Log.i("MainViewModel", list.toString())
            liveData.postValue(list)
        })
    }
    val firebaseTest: LiveData<ArrayList<FolderModule>> = _firebaseTest
}