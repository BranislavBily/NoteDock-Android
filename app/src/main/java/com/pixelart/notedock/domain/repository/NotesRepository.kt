package com.pixelart.notedock.domain.repository

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.usecase.note.NoteModelFromDocumentSnapshotUseCase
import com.pixelart.notedock.domain.usecase.note.NoteModelFromQueryDocumentSnapshotUseCase
import com.pixelart.notedock.model.NoteModel
import io.reactivex.Single

interface NotesRepository {
    fun loadNotes(folderUUID: String, eventListener: EventListener<ArrayList<NoteModel>?>)
    fun loadNote(folderUUID: String, noteUUID: String): Single<NoteModel>
}

class NotesRepositoryImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val firebaseInstance: FirebaseFirestore,
    private val noteModelFromQueryDocumentSnapshotUseCase: NoteModelFromQueryDocumentSnapshotUseCase,
    private val noteModelFromDocumentSnapshotUseCase: NoteModelFromDocumentSnapshotUseCase
) : NotesRepository {
    override fun loadNotes(folderUUID: String,
                           eventListener: EventListener<ArrayList<NoteModel>?>) {
        val notes = ArrayList<NoteModel>()
        firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
            .document(folderUUID)
            .collection(firebaseIDSRepository.getCollectionNotes())
            .addSnapshotListener { queryDocumentSnapshots, _ ->
                queryDocumentSnapshots?.let { querySnapshot ->
                    notes.clear()
                    for (document in querySnapshot) {
                        notes.add(noteModelFromQueryDocumentSnapshotUseCase.getModel(document))
                    }
                    eventListener.onEvent(notes, null)
                }
            }
    }

    override fun loadNote(folderUUID: String,
                          noteUUID: String): Single<NoteModel> {
        return Single.create<NoteModel> { emitter ->
            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .document(noteUUID)
                .get()
                .addOnSuccessListener {
                    val note = noteModelFromDocumentSnapshotUseCase.getNote(it)
                    emitter.onSuccess(note)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}