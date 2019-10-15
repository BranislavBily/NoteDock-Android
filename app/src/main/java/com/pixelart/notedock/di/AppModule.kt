package com.pixelart.notedock.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSImpl
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.repository.FolderRepositoryImpl
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentImpl
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentSnapshotImpl
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentSnapshotUseCase
import com.pixelart.notedock.domain.usecase.FolderModelFromDocumentUseCase
import com.pixelart.notedock.viewModel.FoldersViewViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { FoldersViewViewModel(get())}
}

val firebaseModule = module {
    single { FirebaseIDSImpl() as FirebaseIDSRepository}
    single { FolderModelFromDocumentImpl(get()) as FolderModelFromDocumentUseCase }
    single { FolderRepositoryImpl(get(), get(), get(), FirebaseFirestore.getInstance()) as FolderRepository}
    single { FolderModelFromDocumentSnapshotImpl(get()) as FolderModelFromDocumentSnapshotUseCase }
}