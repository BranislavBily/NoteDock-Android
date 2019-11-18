package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.usecase.AddFolderUseCase
import com.pixelart.notedock.domain.usecase.FolderNameTakenUseCase
import com.pixelart.notedock.model.FolderModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FoldersViewFragmentViewModel(
    private val folderRepository: FolderRepository,
    private val addFolderUseCase: AddFolderUseCase,
    private val folderNameTakenUseCase: FolderNameTakenUseCase
): LifecycleViewModel() {


    private val _firebaseTest = MutableLiveData<ArrayList<FolderModel>>().also { liveData ->
        folderRepository.getFolders(EventListener {list, _ ->
            liveData.postValue(list)
        })
    }
    val firebaseTest: LiveData<ArrayList<FolderModel>> = _firebaseTest

    private val _newFolderCreated = SingleLiveEvent<FolderViewEvent>()
    val newFolderCreated: LiveData<FolderViewEvent> = _newFolderCreated

    private val _isNameTaken = SingleLiveEvent<FolderNameTakenEvent>()
    val isNameTaken : LiveData<FolderNameTakenEvent> = _isNameTaken

    private val _fabClicked = SingleLiveEvent<FABClickedEvent>()
    val fabClicked: LiveData<FABClickedEvent> = _fabClicked
    fun onFABClicked() {
        //Event
        _fabClicked.postValue(FABClickedEvent.Clicked)
    }

    fun uploadFolderModel(folderModel: FolderModel) {
        startStopDisposeBag?.let { bag ->
            addFolderUseCase.addFolder(folderModel)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { _newFolderCreated.postValue(FolderViewEvent.Success(it)) },
                    { _newFolderCreated.postValue(FolderViewEvent.Error) }
                )
                .addTo(bag)
        }
    }

    fun isNameTaken(folderName: String) {
        startStopDisposeBag?.let { bag ->
            folderNameTakenUseCase.isNameTaken(folderName)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        if(!it) {
                            uploadFolderModel(FolderModel(folderName))
                        }
                        _isNameTaken.postValue(FolderNameTakenEvent.Success(it)) },
                    { _isNameTaken.postValue(FolderNameTakenEvent.Error) }
                )
                .addTo(bag)
        }
    }
}

sealed class FolderViewEvent {
    object Error : FolderViewEvent()
    class Success(val uuid: String): FolderViewEvent()
}

sealed class FABClickedEvent {
    object Clicked: FABClickedEvent()
}

sealed class FolderNameTakenEvent {
    class Success(val taken: Boolean) : FolderNameTakenEvent()
    object Error: FolderNameTakenEvent()
}