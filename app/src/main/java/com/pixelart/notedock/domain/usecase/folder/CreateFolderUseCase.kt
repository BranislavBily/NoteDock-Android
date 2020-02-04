package com.pixelart.notedock.domain.usecase.folder

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.FolderModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.*

interface CreateFolderUseCase {
    fun createFolder(user: FirebaseUser, folder: FolderModel): Completable
}

class CreateFolderImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val firebaseInstance: FirebaseFirestore
) : CreateFolderUseCase {

    override fun createFolder(user: FirebaseUser, folder: FolderModel): Completable {
        return Completable.create { emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getFolderName() to folder.name,
                firebaseIDSRepository.getFolderNotesCount() to 0,
                firebaseIDSRepository.getFolderAdded() to Timestamp(Date())
            )

            val listener = firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .document()
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let { emitter.tryOnError(it) }
                    documentSnapshot?.reference?.let { reference ->
                        reference.set(data)
                        emitter.onComplete()
                    }
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
