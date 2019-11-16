package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel
import io.reactivex.Single

interface AddFolderUseCase {
    fun addFolder(folder: FolderModel): Single<String>
}

class AddFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                    private val firebaseInstance: FirebaseFirestore): AddFolderUseCase {

    private val TAG = "AddFolderUseCase"

    override fun addFolder(folder: FolderModel): Single<String> {

        return Single.create<String> {emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getFolderName() to folder.name,
                firebaseIDSRepository.getFolderNotesCount() to "0"
            )

            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .add(data)
                .addOnSuccessListener {emitter.onSuccess(it.id)}
                .addOnFailureListener {emitter.onError(it)}
        }
    }
}