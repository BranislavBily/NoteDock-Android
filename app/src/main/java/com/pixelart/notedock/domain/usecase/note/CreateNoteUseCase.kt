package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import io.reactivex.Single
import io.reactivex.disposables.Disposable

interface CreateNoteUseCase {
    fun createNote(user: FirebaseUser, folderUUID: String): Single<String>
}

class CreateNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): CreateNoteUseCase {
    override fun createNote(user: FirebaseUser, folderUUID: String): Single<String> {
        return Single.create<String> {emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getNoteTitle() to "Untitled",
                firebaseIDSRepository.getNoteDescription() to "",
                firebaseIDSRepository.getNoteMarked() to false,
                firebaseIDSRepository.getNoteUpdated() to FieldValue.serverTimestamp()
            )

            val listener = firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .document()
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let { emitter.tryOnError(it) }

                    documentSnapshot?.reference?.let {reference ->
                        reference.set(data)
                        emitter.onSuccess(reference.id)
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