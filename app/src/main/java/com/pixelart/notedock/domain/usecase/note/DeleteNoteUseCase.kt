package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.viewModel.NoteDeletedEvent
import io.reactivex.Single

interface DeleteNoteUseCase {
    fun deleteNote(folderUUID: String, noteUUID: String): Single<NoteDeletedEvent>
}

class DeleteNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): DeleteNoteUseCase {
    override fun deleteNote(folderUUID: String, noteUUID: String): Single<NoteDeletedEvent> {
        return Single.create<NoteDeletedEvent> { emitter ->
            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .document(noteUUID)
                .delete()
                .addOnSuccessListener { emitter.onSuccess(NoteDeletedEvent.Success)}
                .addOnFailureListener { emitter.onError(it)}
        }
    }
}