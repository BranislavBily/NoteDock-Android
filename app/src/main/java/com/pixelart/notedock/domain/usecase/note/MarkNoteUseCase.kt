package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

interface MarkNoteUseCase {
    fun markNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable
}

class MarkNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                   private val firebaseInstance: FirebaseFirestore
): MarkNoteUseCase {
    override fun markNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable {
        return Completable.create { emitter ->
            note.marked?.let { marked ->
                note.uuid?.let { uuid ->
                    val data = hashMapOf<String, Any>(
                        firebaseIDSRepository.getNoteMarked() to !marked
                    )
                    val listener = firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                            .document(user.uid)
                            .collection(firebaseIDSRepository.getCollectionFolders())
                            .document(folderUUID)
                            .collection(firebaseIDSRepository.getCollectionNotes())
                            .document(uuid)
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

}