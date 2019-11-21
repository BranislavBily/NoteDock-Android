package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.viewModel.FolderDeleteEvent
import io.reactivex.Single

interface DeleteFolderUseCase {
    fun deleteFolder(folderUUID: String): Single<FolderDeleteEvent>
}

class DeleteFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore )
    : DeleteFolderUseCase {

    override fun deleteFolder(folderUUID: String): Single<FolderDeleteEvent> {
        return Single.create { emitter ->
                firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                    .document(folderUUID)
                    .delete()
                    .addOnSuccessListener { emitter.onSuccess(FolderDeleteEvent.Success) }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

