package com.pixelart.notedock.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pixelart.notedock.domain.usecase.note.NoteModelFromDocumentSnapshotUseCase
import com.pixelart.notedock.model.NoteModel
import io.reactivex.Observable
import io.reactivex.Single

interface NotesRepository {
    fun getNotes(user: FirebaseUser, folderUUID: String): Observable<ArrayList<NoteModel>>
    fun loadNote(user: FirebaseUser, folderUUID: String, noteUUID: String): Single<NoteModel>
}

class NotesRepositoryImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val firebaseInstance: FirebaseFirestore,
    private val noteModelFromDocumentSnapshotUseCase: NoteModelFromDocumentSnapshotUseCase
) : NotesRepository {

    override fun getNotes(
        user: FirebaseUser,
        folderUUID: String
    ): Observable<ArrayList<NoteModel>> {
        return Observable.create { emitter ->
            val notes = ArrayList<NoteModel>()
            firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .document(folderUUID)
                .collection(firebaseIDSRepository.getCollectionNotes())
                .orderBy(firebaseIDSRepository.getNoteUpdated(), Query.Direction.DESCENDING)
                .addSnapshotListener { queryDocumentSnapshots, error ->
                    queryDocumentSnapshots?.let { querySnapshot ->
                        notes.clear()
                        for (document in querySnapshot) {
                            notes.add(noteModelFromDocumentSnapshotUseCase.getNote(document))
                        }
                        emitter.onNext(notes)
                    }
                    error?.let {
                        emitter.tryOnError(it)
                    }
                }
        }
    }

    override fun loadNote(
        user: FirebaseUser, folderUUID: String,
        noteUUID: String
    ): Single<NoteModel> {
        return Single.create<NoteModel> { emitter ->
            firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
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