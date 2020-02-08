package com.pixelart.notedock.domain.usecase.folder

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.viewModel.folder.FolderDeleteEvent
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

interface RenameFolderUseCase {
    fun renameFolder(user: FirebaseUser, folderUUID: String, folderName: String): Completable
}

class RenameFolderImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                       private val firebaseInstance: FirebaseFirestore) : RenameFolderUseCase {
    override fun renameFolder(user: FirebaseUser, folderUUID: String, folderName: String): Completable {
        return Completable.create { emitter ->
            val data = hashMapOf<String, Any>(
                firebaseIDSRepository.getFolderName() to folderName
            )
            val listener = firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let { emitter.tryOnError(it) }
                    documentSnapshot?.reference?.update(data)
                    emitter.onComplete()
                }

            emitter.setDisposable(object : Disposable {
                override fun isDisposed(): Boolean {
                    return isDisposed
                }

                override fun dispose() {
                    listener.remove()
                }
            })
        }
    }
}

