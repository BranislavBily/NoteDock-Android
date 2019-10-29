package com.pixelart.notedock.domain.usecase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel

interface AddFolderUseCase {
    fun addFolder(folder: FolderModel)
}

class AddFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                    private val firebaseInstance: FirebaseFirestore): AddFolderUseCase {

    private val TAG = "AddFolderUseCase"

    override fun addFolder(folder: FolderModel) {

        val data = hashMapOf(
            firebaseIDSRepository.getFolderName() to folder.name,
            firebaseIDSRepository.getFolderNotesCount() to "0"
        )

        firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
            .add(data)
            .addOnSuccessListener {
                Log.i(TAG, "Folder added with id of ${it.id}")
            }
            .addOnFailureListener {
                Log.e(TAG, it.message)
            }
    }
}