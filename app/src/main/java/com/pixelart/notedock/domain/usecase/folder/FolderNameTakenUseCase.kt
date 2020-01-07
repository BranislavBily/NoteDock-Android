package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import io.reactivex.Single

interface FolderNameTakenUseCase {
    fun isNameTaken(user: FirebaseUser, folderName: String): Single<Boolean>
}

class FolderNameTakenImpl(private val firebaseFirestore: FirebaseFirestore,
                          private val firebaseIDSRepository: FirebaseIDSRepository):
    FolderNameTakenUseCase {
    override fun isNameTaken(user: FirebaseUser, folderName: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            firebaseFirestore.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .whereEqualTo(firebaseIDSRepository.getFolderName(), folderName)
                .get()
                .addOnSuccessListener { documents ->
                    emitter.onSuccess(!documents.isEmpty)
                }
                .addOnFailureListener {emitter.onError(it) }
        }
    }
}