package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel
import io.reactivex.Single
import java.util.*

interface CreateFolderUseCase {
    fun createFolder(folder: FolderModel): Single<String>
}

class CreateFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore): CreateFolderUseCase {

    override fun createFolder(folder: FolderModel): Single<String> {
        return Single.create { emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getFolderName() to folder.name,
                firebaseIDSRepository.getFolderNotesCount() to 0,
                firebaseIDSRepository.getFolderAdded() to Timestamp(Date())
            )

            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .add(data)
                .addOnSuccessListener { emitter.onSuccess(it.id) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}
