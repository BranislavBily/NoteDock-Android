package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel

interface LoadNotesUseCase {
    fun loadNotes(folderUUID: String, eventListener: EventListener<ArrayList<NoteModel>?>)
}

class LoadNotesImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val firebaseInstance: FirebaseFirestore,
    private val noteModelFromQueryDocumentSnapshotUseCase: NoteModelFromQueryDocumentSnapshotUseCase
) : LoadNotesUseCase {
    override fun loadNotes(
        folderUUID: String,
        eventListener: EventListener<ArrayList<NoteModel>?>
    ) {
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
}