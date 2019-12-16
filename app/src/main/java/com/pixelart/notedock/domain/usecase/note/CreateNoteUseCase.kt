package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.SaveNoteEvent
import io.reactivex.Single

interface AddNoteUseCase {
    fun addNote(folderUUID: String, note: NoteModel): Single<SaveNoteEvent>
}

class AddNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                  private val firebaseInstance: FirebaseFirestore): AddNoteUseCase {
    override fun addNote(folderUUID: String, note: NoteModel): Single<SaveNoteEvent> {
        return Single.create<SaveNoteEvent> {emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getNoteTitle() to note.noteTitle,
                firebaseIDSRepository.getNoteDescription() to note.noteDescription
            )

            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .add(data)
                .addOnSuccessListener { emitter.onSuccess(SaveNoteEvent.Success) }
                .addOnFailureListener { emitter.onError(it)}
        }
    }
}