package com.pixelart.notedock.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.viewModel.FolderNameTakenEvent
import io.reactivex.Single

interface FolderNameTakenUseCase {
    fun isNameTaken(folderName: String): Single<Boolean>
}

class FolderNameTakenImpl(private val firebaseFirestore: FirebaseFirestore,
                          private val firebaseIDSRepository: FirebaseIDSRepository): FolderNameTakenUseCase {
    override fun isNameTaken(folderName: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            firebaseFirestore.collection(firebaseIDSRepository.getCollectionFolders())
                .get()
                .addOnSuccessListener { documents ->
                    var taken = false
                    for (document in documents) {
                        document.getString(firebaseIDSRepository.getFolderName())?.let {
                            if(it == folderName) {
                                taken = true
                            }
                        }
                    }
                    //Skusal som s typom FolderNameTakenEvent ale ta proste to neslo toto ide jej
                    emitter.onSuccess(taken)
                }
                .addOnFailureListener {emitter.onError(it)}
        }
    }
}
