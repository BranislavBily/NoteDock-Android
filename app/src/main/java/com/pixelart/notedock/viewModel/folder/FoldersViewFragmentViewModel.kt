package com.pixelart.notedock.viewModel.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.usecase.folder.CreateFolderUseCase
import com.pixelart.notedock.domain.usecase.folder.DeleteFolderUseCase
import com.pixelart.notedock.domain.usecase.folder.FolderNameTakenUseCase
import com.pixelart.notedock.domain.usecase.folder.RenameFolderUseCase
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.note.GenericCRUDEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FoldersViewFragmentViewModel(
    private val folderRepository: FolderRepository,
    private val auth: FirebaseAuth,
    private val createFolderUseCase: CreateFolderUseCase,
    private val folderNameTakenUseCase: FolderNameTakenUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val renameFolderUseCase: RenameFolderUseCase
) : LifecycleViewModel() {


    private val _loadFolders = MutableLiveData<LoadFoldersEvent>()
    val loadFolders: LiveData<LoadFoldersEvent> = _loadFolders

    private val _newFolderCreated = MutableLiveData<CreateFolderEvent>()
    val newFolderCreated: LiveData<CreateFolderEvent> = _newFolderCreated

    private val _renameFolder = MutableLiveData<RenameFolderEvent>()
    val renameFolder: LiveData<RenameFolderEvent> = _renameFolder

    private val _deleteFolder = MutableLiveData<GenericCRUDEvent>()
    val deleteFolder: LiveData<GenericCRUDEvent> = _deleteFolder

    private val _fabClicked = MutableLiveData<ButtonPressedEvent>()
    val fabClicked: LiveData<ButtonPressedEvent> = _fabClicked

    fun onFABClicked() {
        _fabClicked.postValue(ButtonPressedEvent.Pressed())
    }

    override fun onStartStopObserve(disposeBag: CompositeDisposable) {
        super.onStartStopObserve(disposeBag)

        loadFolders(disposeBag)
    }

    private fun loadFolders(disposeBag: CompositeDisposable) {
        auth.currentUser?.let { user ->
            folderRepository.getFolders(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ _loadFolders.postValue(LoadFoldersEvent.Success(it)) },
                    { error ->
                        Crashlytics.logException(error)
                        _loadFolders.postValue(LoadFoldersEvent.Error())
                }).addTo(disposeBag)
        } ?: run {
            _loadFolders.postValue(LoadFoldersEvent.NoUserFound())
        }
    }

    private fun uploadFolderModel(folderName: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                createFolderUseCase.createFolder(user, folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _newFolderCreated.postValue(CreateFolderEvent.Success()) },
                        { error ->
                            Crashlytics.logException(error)
                            _newFolderCreated.postValue(CreateFolderEvent.Error())
                        }
                    )
                    .addTo(bag)
            }
        } ?: run {
            _newFolderCreated.postValue(CreateFolderEvent.NoUserFound())
        }

    }

    fun isNameTaken(folderName: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                folderNameTakenUseCase.isNameTaken(user, folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({ nameTaken ->
                        if(nameTaken) _newFolderCreated.postValue(CreateFolderEvent.FolderNameTaken())
                        else this.uploadFolderModel(folderName)
                    }, { error ->
                        Crashlytics.logException(error)
                        _newFolderCreated.postValue(CreateFolderEvent.Error())
                    })
                    .addTo(bag)
            }
        } ?: run {
            _newFolderCreated.postValue(CreateFolderEvent.NoUserFound())
        }
    }

    fun renameFolder(folderUUID: String, folderName: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                folderNameTakenUseCase.isNameTaken(user, folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({ nameTaken ->
                        if(nameTaken) _renameFolder.postValue(RenameFolderEvent.FolderNameTaken())
                        else this.updateFolderName(user, folderUUID, folderName)
                    }, { error ->
                        Crashlytics.logException(error)
                        _renameFolder.postValue(RenameFolderEvent.Error())
                    })
                    .addTo(bag)
            }
        } ?: run {
            _renameFolder.postValue(RenameFolderEvent.NoUserFound())
        }
    }

    fun deleteFolder(folderUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                deleteFolderUseCase.deleteFolder(user, folderUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        _deleteFolder.postValue(GenericCRUDEvent.Success())
                    }, { error ->
                        Crashlytics.logException(error)
                        _deleteFolder.postValue(GenericCRUDEvent.Error())
                    })
                    .addTo(bag)
            }
        } ?: run {
            _deleteFolder.postValue(GenericCRUDEvent.NoUserFound())
        }
    }

    private fun updateFolderName(user: FirebaseUser, folderUUID: String, folderName: String) {
        startStopDisposeBag?.let { bag ->
            renameFolderUseCase.renameFolder(user, folderUUID, folderName)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    _renameFolder.postValue(RenameFolderEvent.Success())
                }, { error ->
                    Crashlytics.logException(error)
                    _renameFolder.postValue(RenameFolderEvent.Error())
                })
                .addTo(bag)
        }
    }
}

sealed class LoadFoldersEvent: Event() {
    class Success(val folders: ArrayList<FolderModel>): LoadFoldersEvent()
    class Error : LoadFoldersEvent()
    class NoUserFound: LoadFoldersEvent()
}

sealed class CreateFolderEvent : Event() {
    class Error : CreateFolderEvent()
    class Success : CreateFolderEvent()
    class NoUserFound: CreateFolderEvent()
    class FolderNameTaken: CreateFolderEvent()
}

sealed class RenameFolderEvent: Event() {
    class Success: RenameFolderEvent()
    class Error: RenameFolderEvent()
    class NoUserFound: RenameFolderEvent()
    class FolderNameTaken: RenameFolderEvent()
}