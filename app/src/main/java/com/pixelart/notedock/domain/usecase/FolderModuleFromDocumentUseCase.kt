package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel

interface FolderModuleFromDocumentUseCase {
    fun getModule(documentSnapshot: QueryDocumentSnapshot): FolderModel
}

class FolderModuleFromDocumentImpl(private val firebaseIDSRepository: FirebaseIDSRepository): FolderModuleFromDocumentUseCase {
    override fun getModule(documentSnapshot: QueryDocumentSnapshot): FolderModel {
        val folder = FolderModel()
        documentSnapshot.getString(firebaseIDSRepository.getFolderName())?.let {
            folder.name = it
        }
        documentSnapshot.getString(firebaseIDSRepository.getFolderNotesCount())?.let {
            folder.notesCount = it
        }
        return folder
    }

}