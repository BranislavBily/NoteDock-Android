package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

interface UpdateNoteUseCase {
    fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable
}

class UpdateNoteImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val firebaseInstance: FirebaseFirestore
) : UpdateNoteUseCase {

    override fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable {
        return Completable.create { emitter ->
            note.uuid?.let { noteUUID ->
                val data = hashMapOf(
                    firebaseIDSRepository.getNoteTitle() to note.noteTitle,
                    firebaseIDSRepository.getNoteDescription() to note.noteDescription,
                    firebaseIDSRepository.getNoteUpdated() to FieldValue.serverTimestamp()
                )
                val listener =
                    firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                        .document(user.uid)
                        .collection(firebaseIDSRepository.getCollectionFolders())
                        .document(folderUUID)
                        .collection(firebaseIDSRepository.getCollectionNotes())
                        .document(noteUUID)
                        .addSnapshotListener { snapshot, firebaseException ->
                            firebaseException?.let { emitter.tryOnError(it) }
                            snapshot?.reference?.update(data)
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
}