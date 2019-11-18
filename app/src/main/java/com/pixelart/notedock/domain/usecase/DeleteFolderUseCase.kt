package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.FolderDeleteEvent
import io.reactivex.Single

interface DeleteFolderUseCase {
    fun deleteFolder(folderModel: FolderModel): Single<FolderDeleteEvent>
}

class DeleteFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore): DeleteFolderUseCase {

    override fun deleteFolder(folderModel: FolderModel): Single<FolderDeleteEvent> {
        return Single.create { emitter ->
            folderModel.uid?.let { uid ->
                firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                    .document(uid)
                    .delete()
                    .addOnSuccessListener { emitter.onSuccess(FolderDeleteEvent.Success) }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }
}

