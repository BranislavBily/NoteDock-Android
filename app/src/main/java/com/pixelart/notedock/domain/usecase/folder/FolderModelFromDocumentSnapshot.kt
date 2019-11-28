package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.firestore.DocumentSnapshot
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel

interface FolderModelFromDocumentSnapshotUseCase {
    fun getModel(documentSnapshot: DocumentSnapshot): FolderModel
}

class FolderModelFromDocumentSnapshotImpl(private val firebaseIDSRepository: FirebaseIDSRepository):
    FolderModelFromDocumentSnapshotUseCase {
    override fun getModel(documentSnapshot: DocumentSnapshot): FolderModel {
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