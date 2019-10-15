package com.pixelart.notedock.domain.repository

import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentSnapshotUseCase
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentUseCase
import com.pixelart.notedock.model.FolderModel

interface FolderRepository {
    fun getFolders(eventListener: EventListener<ArrayList<FolderModel>?>)
    fun getFolder(uid: String, eventListener: EventListener<FolderModel>)
}

class FolderRepositoryImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val folderModelFromDocumentUseCase: FolderModelFromDocumentUseCase,
    private val folderModelFromDocumentSnapshotUseCase: FolderModelFromDocumentSnapshotUseCase,
    private val firebaseInstance: FirebaseFirestore
) : FolderRepository {

    private val TAG = "FolderRepository"

    override fun getFolder(uid: String, eventListener: EventListener<FolderModel>) {
        firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val folder = folderModelFromDocumentSnapshotUseCase.getModel(documentSnapshot)
                eventListener.onEvent(folder, null)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message)
            }
    }

    override fun getFolders(eventListener: EventListener<ArrayList<FolderModel>?>) {
        val folders = ArrayList<FolderModel>()
        firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
            .addSnapshotListener { queryDocumentSnapshots, _ ->
                queryDocumentSnapshots?.let {
                    folders.clear()
                    for (documentSnapshot in it) {
                        folders.add(folderModelFromDocumentUseCase.getModel(documentSnapshot))
                    }
                    eventListener.onEvent(folders, null)
                }
            }
    }
}