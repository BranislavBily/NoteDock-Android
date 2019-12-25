package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import io.reactivex.Single

interface FolderNameTakenUseCase {
    fun isNameTaken(folderName: String): Single<Boolean>
}

class FolderNameTakenImpl(private val firebaseFirestore: FirebaseFirestore,
                          private val firebaseIDSRepository: FirebaseIDSRepository):
    FolderNameTakenUseCase {
    override fun isNameTaken(folderName: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            firebaseFirestore.collection(firebaseIDSRepository.getCollectionFolders())
                .whereEqualTo(firebaseIDSRepository.getFolderName(), folderName)
                .get()
                .addOnSuccessListener { documents ->
                    emitter.onSuccess(!documents.isEmpty)
                }
                .addOnFailureListener {emitter.onError(it)}
        }
    }
}