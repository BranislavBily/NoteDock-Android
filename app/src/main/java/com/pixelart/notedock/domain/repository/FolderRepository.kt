package com.pixelart.notedock.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pixelart.notedock.domain.usecase.folder.FolderModelFromDocumentSnapshotUseCase
import com.pixelart.notedock.domain.usecase.folder.FolderModelFromDocumentUseCase
import com.pixelart.notedock.model.FolderModel
import io.reactivex.Observable
import io.reactivex.Single

interface FolderRepository {
    fun getFolders(user: FirebaseUser): Observable<ArrayList<FolderModel>>
    fun getFolder(uid: String): Single<FolderModel>
}

class FolderRepositoryImpl(
    private val firebaseIDSRepository: FirebaseIDSRepository,
    private val folderModelFromDocumentUseCase: FolderModelFromDocumentUseCase,
    private val folderModelFromDocumentSnapshotUseCase: FolderModelFromDocumentSnapshotUseCase,
    private val firebaseInstance: FirebaseFirestore
) : FolderRepository {

    override fun getFolder(uid: String): Single<FolderModel> {
        return Single.create<FolderModel> { emitter ->
            firebaseInstance.collection(firebaseIDSRepository.getCollectionFolders())
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val folder = folderModelFromDocumentSnapshotUseCase.getModel(documentSnapshot)
                    emitter.onSuccess(folder)
                }
                .addOnFailureListener { exception -> emitter.onError(exception) }
        }
    }

    override fun getFolders(user: FirebaseUser): Observable<ArrayList<FolderModel>> {
        return Observable.create { emitter ->
            val folders = ArrayList<FolderModel>()
            firebaseInstance.collection(firebaseIDSRepository.getCollectionUsers())
                .document(user.uid)
                .collection(firebaseIDSRepository.getCollectionFolders())
                .orderBy(firebaseIDSRepository.getFolderAdded(), Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    querySnapshot?.let {
                        folders.clear()
                        for (documentSnapshot in it) {
                            folders.add(folderModelFromDocumentUseCase.getModel(documentSnapshot))
                        }
                        emitter.onNext(folders)
                    }
                    error?.let {
                        emitter.tryOnError(it)
                    }
                }
        }
    }
}