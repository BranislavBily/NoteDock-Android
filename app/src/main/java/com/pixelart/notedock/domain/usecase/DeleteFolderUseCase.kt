package com.pixelart.notedock.domain.usecase

import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel
import java.lang.Exception

interface DeleteFolderUseCase {
    fun deleteFolder(folderModel: FolderModel, eventListener: EventListener<Exception>)
}

class DeleteFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore): DeleteFolderUseCase {

    private val TAG = "DeleteFolderUseCase"

    override fun deleteFolder(folderModel: FolderModel, eventListener: EventListener<Exception>) {
        folderModel.uid?.let { uid ->
            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(uid)
                .delete()
                .addOnSuccessListener {
                    Log.i(TAG, "Document $uid successfully deleted")
                    eventListener.onEvent(null, null)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message)
                    eventListener.onEvent(it, null)
                }
        }
    }
}