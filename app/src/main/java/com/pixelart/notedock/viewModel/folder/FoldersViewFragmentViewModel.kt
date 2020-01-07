package com.pixelart.notedock.viewModel.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.usecase.folder.CreateFolderUseCase
import com.pixelart.notedock.domain.usecase.folder.FolderNameTakenUseCase
import com.pixelart.notedock.model.FolderModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FoldersViewFragmentViewModel(
    private val folderRepository: FolderRepository,
    private val auth: FirebaseAuth,
    private val createFolderUseCase: CreateFolderUseCase,
    private val folderNameTakenUseCase: FolderNameTakenUseCase
): LifecycleViewModel() {


    private val _loadFolders = MutableLiveData<ArrayList<FolderModel>>().also { liveData ->
        folderRepository.getFolders(EventListener {list, _ ->
            liveData.postValue(list)
        })
    }
    val loadFolders: LiveData<ArrayList<FolderModel>> = _loadFolders

    private val _newFolderCreated = SingleLiveEvent<CreateFolderEvent>()
    val newFolderCreated: LiveData<CreateFolderEvent> = _newFolderCreated

    private val _isNameTaken = SingleLiveEvent<FolderNameTakenEvent>()
    val isNameTaken : LiveData<FolderNameTakenEvent> = _isNameTaken

    private val _fabClicked = SingleLiveEvent<FABClickedEvent>()
    val fabClicked: LiveData<FABClickedEvent> = _fabClicked

    fun onFABClicked() {
        _fabClicked.postValue(FABClickedEvent.Clicked)
    }

    fun uploadFolderModel(folderModel: FolderModel) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                createFolderUseCase.createFolder(user, folderModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _newFolderCreated.postValue(CreateFolderEvent.Success(it)) },
                        { _newFolderCreated.postValue(CreateFolderEvent.Error) }
                    )
                    .addTo(bag)
            }
        }

    }

    fun isNameTaken(folderName: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                folderNameTakenUseCase.isNameTaken(user, folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _isNameTaken.postValue(FolderNameTakenEvent.Success( it, folderName)) },
                        { _isNameTaken.postValue(FolderNameTakenEvent.Error) }
                    )
                    .addTo(bag)
            }
        }
    }
}

sealed class CreateFolderEvent {
    object Error : CreateFolderEvent()
    class Success(val uuid: String): CreateFolderEvent()
}

sealed class FABClickedEvent {
    object Clicked: FABClickedEvent()
}

sealed class FolderNameTakenEvent {
    class Success(val taken: Boolean, val folderName: String) : FolderNameTakenEvent()
    object Error: FolderNameTakenEvent()
}