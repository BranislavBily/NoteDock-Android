package com.pixelart.notedock.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSImpl
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.repository.FolderRepositoryImpl
import com.pixelart.notedock.domain.usecase.*
import com.pixelart.notedock.viewModel.FolderFragmentViewModel
import com.pixelart.notedock.viewModel.FoldersViewFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { FolderFragmentViewModel(get(), get()) }
    viewModel { FoldersViewFragmentViewModel(get(), get(), get()) }
}

val firebaseModule = module {
    single { FirebaseIDSImpl() as FirebaseIDSRepository }
    single { FolderModelFromDocumentImpl(get()) as FolderModelFromDocumentUseCase }
    single { FolderRepositoryImpl(get(), get(), get(), FirebaseFirestore.getInstance()) as FolderRepository }
    single { FolderModelFromDocumentSnapshotImpl(get()) as FolderModelFromDocumentSnapshotUseCase }
    single { AddFolderImpl(get(), FirebaseFirestore.getInstance()) as AddFolderUseCase }
    single { DeleteFolderImpl(get(), FirebaseFirestore.getInstance()) as DeleteFolderUseCase }
    single {FolderNameTakenImpl(FirebaseFirestore.getInstance(), get()) as FolderNameTakenUseCase }
}