package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.SaveNoteEvent
import io.reactivex.Single
import java.lang.NullPointerException

interface UpdateNoteUseCase {
    fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Single<SaveNoteEvent>
}

class UpdateNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): UpdateNoteUseCase {

    override fun updateNote(user: FirebaseUser, folderUUID: String, note: NoteModel): Single<SaveNoteEvent> {
        return Single.create<SaveNoteEvent> { emitter ->
            note.uuid?.let { noteUID ->
                firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                    .document(user.uid)
                    .collection(firebaseIDSRepository.getCollectionFolders())
                    .document(folderUUID)
                    .collection(firebaseIDSRepository.getCollectionNotes())
                    .document(noteUID)
                    .update(mapOf(
                        firebaseIDSRepository.getNoteTitle() to note.noteTitle,
                        firebaseIDSRepository.getNoteDescription() to note.noteDescription
                    ))
                    .addOnSuccessListener { emitter.onSuccess(SaveNoteEvent.Success()) }
                    .addOnFailureListener { emitter.tryOnError(it)}
            } ?: emitter.tryOnError(NullPointerException("Note UUID is null!"))
        }
    }
}