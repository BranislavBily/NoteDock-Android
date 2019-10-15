package com.pixelart.notedock.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.FirebaseIDSImpl
import com.pixelart.notedock.domain.repository.FirebaseIDSRepository
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.repository.FolderRepositoryImpl
import com.pixelart.notedock.domain.usecase.FolderModuleFromDocumentImpl
import com.pixelart.notedock.domain.usecase.FolderModuleFromDocumentUseCase
import com.pixelart.notedock.domain.usecase.KoinTestingImpl
import com.pixelart.notedock.domain.usecase.KoinTestingUseCase
import com.pixelart.notedock.viewModel.FoldersViewViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { KoinTestingImpl() as KoinTestingUseCase}
    viewModel { FoldersViewViewModel(get())}
}

val firebaseModule = module {
    single { FirebaseIDSImpl() as FirebaseIDSRepository}
    single {FolderModuleFromDocumentImpl(get()) as FolderModuleFromDocumentUseCase}
    single {FolderRepositoryImpl(get(), get(), FirebaseFirestore.getInstance()) as FolderRepository}
}