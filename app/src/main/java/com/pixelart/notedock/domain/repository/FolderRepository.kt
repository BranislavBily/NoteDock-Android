package com.pixelart.notedock.domain.repository

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.usecase.FolderModuleFromDocumentUseCase
import com.pixelart.notedock.model.FolderModel

interface FolderRepository {
    fun getFolders(eventListener: EventListener<ArrayList<FolderModel>?>)
}

class FolderRepositoryImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val folderModuleFromDocumentUseCase: FolderModuleFromDocumentUseCase,
    private val firebaseInstance: FirebaseFirestore
) : FolderRepository {
    override fun getFolders(eventListener: EventListener<ArrayList<FolderModel>?>) {
        val folders = ArrayList<FolderModel>()
        firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
            .addSnapshotListener { queryDocumentSnapshots, _ ->
                queryDocumentSnapshots?.let {
                    folders.clear()
                    for (documentSnapshot in it) {
                        folders.add(folderModuleFromDocumentUseCase.getModule(documentSnapshot))
                    }
                    eventListener.onEvent(folders, null)
                }
            }
    }
}