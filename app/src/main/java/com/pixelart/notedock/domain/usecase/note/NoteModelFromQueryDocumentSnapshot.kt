package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel

interface NoteModelFromQueryDocumentSnapshotUseCase {
    fun getModel(queryDocumentSnapshot: QueryDocumentSnapshot): NoteModel
}

class NoteModelFromQueryDocumentSnapshotImpl(private val firebaseIDSRepository: FirebaseIDSRepository) :
    NoteModelFromQueryDocumentSnapshotUseCase {
    override fun getModel(queryDocumentSnapshot: QueryDocumentSnapshot): NoteModel {
        val note = NoteModel()

        note.uuid = queryDocumentSnapshot.id

        queryDocumentSnapshot.getString(firebaseIDSRepository.getNoteTitle())?.let {
            note.noteTitle = it
        }

        queryDocumentSnapshot.getString(firebaseIDSRepository.getNoteDescription())?.let {
            note.noteDescription = it
        }
        return note
    }
}