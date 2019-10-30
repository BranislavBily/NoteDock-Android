package com.pixelart.notedock.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.model.FolderModel

class FoldersViewFragmentViewModel(private val folderRepository: FolderRepository): ViewModel() {

    private val _firebaseTest = MutableLiveData<ArrayList<FolderModel>>().also { liveData ->
        folderRepository.getFolders(EventListener {list, _ ->
            liveData.postValue(list)
        })
    }
    val firebaseTest: LiveData<ArrayList<FolderModel>> = _firebaseTest
}