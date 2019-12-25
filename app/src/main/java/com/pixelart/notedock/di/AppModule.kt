package com.pixelart.notedock.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.*
import com.pixelart.notedock.domain.usecase.folder.*
import com.pixelart.notedock.domain.usecase.note.*
import com.pixelart.notedock.viewModel.FolderFragmentViewModel
import com.pixelart.notedock.viewModel.FoldersViewFragmentViewModel
import com.pixelart.notedock.viewModel.NoteFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { FolderFragmentViewModel(get(), get(), get()) }
    viewModel { FoldersViewFragmentViewModel(get(), get(), get()) }
    viewModel { NoteFragmentViewModel(get(), get(), get())}
}

val firebaseModule = module {
    //Repository
    single { FirebaseIDSImpl() as FirebaseIDSRepository }
    single { FolderRepositoryImpl(get(), get(), get(), FirebaseFirestore.getInstance()) as FolderRepository }
    single { NotesRepositoryImpl(get(), FirebaseFirestore.getInstance(), get(), get()) as NotesRepository }

    //UseCase Folder
    single { FolderModelFromDocumentImpl(get()) as FolderModelFromDocumentUseCase }
    single { FolderModelFromDocumentSnapshotImpl(get()) as FolderModelFromDocumentSnapshotUseCase }
    single { CreateFolderImpl(get(), FirebaseFirestore.getInstance()) as CreateFolderUseCase }
    single { DeleteFolderImpl(get(), FirebaseFirestore.getInstance()) as DeleteFolderUseCase }
    single { FolderNameTakenImpl(FirebaseFirestore.getInstance(), get()) as FolderNameTakenUseCase }
    //Note
    single { CreateNoteImpl(get(), FirebaseFirestore.getInstance()) as CreateNoteUseCase }
    single { DeleteNoteImpl(get(), FirebaseFirestore.getInstance()) as DeleteNoteUseCase }
    single { NoteModelFromQueryDocumentSnapshotImpl(get()) as NoteModelFromQueryDocumentSnapshotUseCase }
    single { NoteModelFromDocumentSnapshotImpl(get()) as NoteModelFromDocumentSnapshotUseCase }
    single { UpdateNoteImpl(get(), FirebaseFirestore.getInstance()) as UpdateNoteUseCase }
}