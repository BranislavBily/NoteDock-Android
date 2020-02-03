package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.SaveNoteEvent
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.NullPointerException

interface UpdateNoteUseCase {
    fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable
}

class UpdateNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): UpdateNoteUseCase {

    override fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Completable {
        return Completable.create { emitter ->
            note.uuid?.let { noteUID ->
                firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                    .document(user.uid)
                    .collection(firebaseIDSRepository.getCollectionFolders())
                    .document(folderUUID)
                    .collection(firebaseIDSRepository.getCollectionNotes())
                    .document(noteUID)
                    .update(mapOf(
                        firebaseIDSRepository.getNoteTitle() to note.noteTitle,
                        firebaseIDSRepository.getNoteDescription() to note.noteDescription,
                        firebaseIDSRepository.getNoteUpdated() to FieldValue.serverTimestamp()
                    ))
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.tryOnError(it)}
            } ?: emitter.tryOnError(NullPointerException("Note UUID is null!"))
        }
    }
}