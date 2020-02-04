package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

interface DeleteNoteUseCase {
    fun deleteNote(user: FirebaseUser, folderUUID: String, noteUUID: String): Completable
}

class DeleteNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): DeleteNoteUseCase {
    override fun deleteNote(user: FirebaseUser, folderUUID: String, noteUUID: String): Completable {
        return Completable.create { emitter ->
            val listener = firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .document(noteUUID)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        emitter.tryOnError(it)
                    }

                    documentSnapshot?.reference?.delete()
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