package com.pixelart.notedock.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelart.notedock.domain.repository.*
import com.pixelart.notedock.domain.usecase.folder.*
import com.pixelart.notedock.domain.usecase.note.*
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.RegisterFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.ResetPasswordFragmentViewModel
import com.pixelart.notedock.viewModel.folder.FolderFragmentViewModel
import com.pixelart.notedock.viewModel.folder.FoldersViewFragmentViewModel
import com.pixelart.notedock.viewModel.note.NoteFragmentViewModel
import com.pixelart.notedock.viewModel.settings.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    // Folder
    viewModel { (folderUUID: String, folderName: String) ->
        FolderFragmentViewModel(
            folderUUID = folderUUID,
            folderName = folderName,
            auth = FirebaseAuth.getInstance(),
            deleteNoteUseCase = get(),
            createFolderUseCase = get(),
            markNoteUseCase = get(),
            notesRepository = get(),
        )
    }
    viewModelOf(::FoldersViewFragmentViewModel)
    // Note
    viewModel { (folderUUID: String, noteUUID: String) ->
        NoteFragmentViewModel(
            folderUUID = folderUUID,
            noteUUID = noteUUID,
            notesRepository = get(),
            auth = FirebaseAuth.getInstance(),
            deleteNoteUseCase = get(),
            updateNoteUseCase = get(),
        )
    }
    // Authentication
    viewModelOf(::LoginFragmentViewModel)
    viewModelOf(::RegisterFragmentViewModel)
    viewModelOf(::ResetPasswordFragmentViewModel)

    // Settings
    viewModelOf(::SettingsFragmentViewModel)
    viewModelOf(::AccountSettingsViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::HelpAndSupportViewModel)
    viewModelOf(::ChangeEmailViewModel)
    viewModelOf(::DeleteAccountViewModel)
}

val firebaseModule = module {
    // Repository
    single<FirebaseIDSRepository> { FirebaseIDSImpl() }

    single {
        FirebaseFirestore.getInstance()
    }

    singleOf(::FolderRepositoryImpl) bind FolderRepository::class
    singleOf(::NotesRepositoryImpl) bind NotesRepository::class

    // UseCase Folder
    singleOf(::FolderModelFromDocumentImpl) bind FolderModelFromDocumentUseCase::class
    singleOf(::FolderModelFromDocumentSnapshotImpl) bind FolderModelFromDocumentSnapshotUseCase::class
    singleOf(::CreateFolderImpl) bind CreateFolderUseCase::class
    singleOf(::DeleteFolderImpl) bind DeleteFolderUseCase::class
    singleOf(::FolderNameTakenImpl) bind FolderNameTakenUseCase::class
    singleOf(::RenameFolderImpl) bind RenameFolderUseCase::class

    // Note
    singleOf(::CreateNoteImpl) bind CreateNoteUseCase::class
    singleOf(::DeleteNoteImpl) bind DeleteNoteUseCase::class
    singleOf(::NoteModelFromQueryDocumentSnapshotImpl) bind NoteModelFromQueryDocumentSnapshotUseCase::class
    singleOf(::NoteModelFromDocumentSnapshotImpl) bind NoteModelFromDocumentSnapshotUseCase::class
    singleOf(::UpdateNoteImpl) bind UpdateNoteUseCase::class
    singleOf(::MarkNoteImpl) bind MarkNoteUseCase::class

    // Authentication
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
