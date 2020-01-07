package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.viewModel.folder.FolderDeleteEvent
import io.reactivex.Single

interface DeleteFolderUseCase {
    fun deleteFolder(user: FirebaseUser, folderUUID: String): Single<FolderDeleteEvent>
}

class DeleteFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore )
    : DeleteFolderUseCase {

    override fun deleteFolder(user: FirebaseUser, folderUUID: String): Single<FolderDeleteEvent> {
        return Single.create { emitter ->
                firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                    .document(user.uid)
                    .collection(firebaseIDSRepository.getCollectionFolders())
                    .document(folderUUID)
                    .delete()
                    .addOnSuccessListener { emitter.onSuccess(FolderDeleteEvent.Success()) }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

