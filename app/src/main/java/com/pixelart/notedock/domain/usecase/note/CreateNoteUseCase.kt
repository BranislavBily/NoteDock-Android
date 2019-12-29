package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import io.reactivex.Single
import java.util.*

interface CreateNoteUseCase {
    fun createNote(folderUUID: String): Single<String>
}

class CreateNoteImpl(private val firebaseIDSRepository: FirebaseIDSRepository,
                     private val firebaseInstance: FirebaseFirestore): CreateNoteUseCase {
    override fun createNote(folderUUID: String): Single<String> {
        return Single.create<String> {emitter ->
            val data = hashMapOf(
                firebaseIDSRepository.getNoteTitle() to "Untitled",
                firebaseIDSRepository.getNoteAdded() to Timestamp(Date())
            )

            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .add(data)
                .addOnSuccessListener { documentReference ->
                    emitter.onSuccess(documentReference.id)
                 }
                .addOnFailureListener { emitter.onError(it)}
        }
    }
}