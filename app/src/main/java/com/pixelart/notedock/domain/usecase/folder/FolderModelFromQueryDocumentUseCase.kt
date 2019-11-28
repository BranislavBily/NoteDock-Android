package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel

interface FolderModelFromDocumentUseCase {
    fun getModel(documentSnapshot: QueryDocumentSnapshot): FolderModel
}

class FolderModelFromDocumentImpl(private val firebaseIDSRepository: FirebaseIDSRepository):
    FolderModelFromDocumentUseCase {
    override fun getModel(documentSnapshot: QueryDocumentSnapshot): FolderModel {
        val folder = FolderModel()

        folder.uid = documentSnapshot.id

        documentSnapshot.getString(firebaseIDSRepository.getFolderName())?.let {
            folder.name = it
        }
        documentSnapshot.getString(firebaseIDSRepository.getFolderNotesCount())?.let {
            folder.notesCount = it
        }
        return folder
    }
}