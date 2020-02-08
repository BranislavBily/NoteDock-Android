package com.pixelart.notedock.domain.usecase.note

import com.google.firebase.firestore.DocumentSnapshot
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.model.NoteModel

interface NoteModelFromDocumentSnapshotUseCase {
    fun getNote(document: DocumentSnapshot): NoteModel
}

class NoteModelFromDocumentSnapshotImpl(private val firebaseIDSRepository: FirebaseIDSRepository):
    NoteModelFromDocumentSnapshotUseCase {

    override fun getNote(document: DocumentSnapshot): NoteModel {
        val note = NoteModel()
        note.uuid = document.id

        document.getString(firebaseIDSRepository.getNoteTitle())?.let {
            note.noteTitle = it
        }

        document.getString(firebaseIDSRepository.getNoteDescription())?.let {
            note.noteDescription = it
        }

        document.getBoolean(firebaseIDSRepository.getNoteMarked())?.let {
            note.marked = it
        }

        return note
    }
}