package com.pixelart.notedock.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.*
import com.pixelart.notedock.domain.usecase.folder.*
import com.pixelart.notedock.domain.usecase.note.*
import com.pixelart.notedock.viewModel.folder.FolderFragmentViewModel
import com.pixelart.notedock.viewModel.folder.FoldersViewFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
import com.pixelart.notedock.viewModel.NoteFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.ResetPasswordFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.RegisterFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (folderUUID: String, folderName: String) ->
        FolderFragmentViewModel(
            folderUUID = folderUUID,
            folderName = folderName,
            auth = FirebaseAuth.getInstance(),
            deleteFolderUseCase = get(),
            createFolderUseCase = get(),
            notesRepository = get()
        )
    }
    viewModel {
        FoldersViewFragmentViewModel(
            folderRepository = get(),
            auth = FirebaseAuth.getInstance(),
            createFolderUseCase = get(),
            folderNameTakenUseCase = get()
        )
    }
    viewModel {
        NoteFragmentViewModel(
            notesRepository = get(),
            auth = FirebaseAuth.getInstance(),
            deleteNoteUseCase = get(),
            updateNoteUseCase = get()
        )
    }
    viewModel {
        LoginFragmentViewModel(
            authRepository = get(),
            auth = FirebaseAuth.getInstance()
            )
    }
    viewModel {
        RegisterFragmentViewModel(
            authRepository = get(),
            auth = FirebaseAuth.getInstance()
        )
    }
    viewModel {
        ResetPasswordFragmentViewModel(
            authRepository = get()
        )
    }
}

val firebaseModule = module {
    //Repository
    single { FirebaseIDSImpl() as FirebaseIDSRepository }
    single {
        FolderRepositoryImpl(
            firebaseIDSRepository = get(),
            folderModelFromDocumentSnapshotUseCase = get(),
            folderModelFromDocumentUseCase = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
            ) as FolderRepository
    }
    single {
        NotesRepositoryImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance(),
            noteModelFromDocumentSnapshotUseCase = get()
        ) as NotesRepository
    }

    //UseCase Folder
    single {
        FolderModelFromDocumentImpl(
            firebaseIDSRepository = get()
        ) as FolderModelFromDocumentUseCase
    }
    single {
        FolderModelFromDocumentSnapshotImpl(
            firebaseIDSRepository = get()
        ) as FolderModelFromDocumentSnapshotUseCase
    }
    single {
        CreateFolderImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
        ) as CreateFolderUseCase
    }
    single {
        DeleteFolderImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
        ) as DeleteFolderUseCase
    }
    single {
        FolderNameTakenImpl(
            firebaseFirestore = FirebaseFirestore.getInstance(),
            firebaseIDSRepository = get()
        ) as FolderNameTakenUseCase
    }
    //Note
    single {
        CreateNoteImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
        ) as CreateNoteUseCase
    }
    single {
        DeleteNoteImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
        ) as DeleteNoteUseCase
    }
    single {
        NoteModelFromQueryDocumentSnapshotImpl(
            firebaseIDSRepository = get()
        ) as NoteModelFromQueryDocumentSnapshotUseCase
    }
    single {
        NoteModelFromDocumentSnapshotImpl(
            firebaseIDSRepository = get()
        ) as NoteModelFromDocumentSnapshotUseCase
    }
    single {
        UpdateNoteImpl(
            firebaseIDSRepository = get(),
            firebaseInstance = FirebaseFirestore.getInstance()
        ) as UpdateNoteUseCase
    }

    //Authentication
    single {
        AuthRepositoryImpl(
            auth = FirebaseAuth.getInstance()
        ) as AuthRepository
    }
}