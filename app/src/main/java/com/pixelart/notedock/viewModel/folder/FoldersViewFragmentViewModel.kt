package com.pixelart.notedock.viewModel.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.domain.usecase.folder.CreateFolderUseCase
import com.pixelart.notedock.domain.usecase.folder.FolderNameTakenUseCase
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FoldersViewFragmentViewModel(
    private val folderRepository: FolderRepository,
    private val auth: FirebaseAuth,
    private val createFolderUseCase: CreateFolderUseCase,
    private val folderNameTakenUseCase: FolderNameTakenUseCase
) : LifecycleViewModel() {


    private val _loadFolders = MutableLiveData<ArrayList<FolderModel>>()
    val loadFolders: LiveData<ArrayList<FolderModel>> = _loadFolders

    private val _newFolderCreated = MutableLiveData<CreateFolderEvent>()
    val newFolderCreated: LiveData<CreateFolderEvent> = _newFolderCreated

    private val _isNameTaken = MutableLiveData<FolderNameTakenEvent>()
    val isNameTaken: LiveData<FolderNameTakenEvent> = _isNameTaken

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
                .subscribe({ _loadFolders.postValue(it) }, {

                }).addTo(disposeBag)
        }
    }

    fun uploadFolderModel(folderModel: FolderModel) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                createFolderUseCase.createFolder(user, folderModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _newFolderCreated.postValue(CreateFolderEvent.Success()) },
                        { _newFolderCreated.postValue(CreateFolderEvent.Error()) }
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
                        { _isNameTaken.postValue(FolderNameTakenEvent.Success(it, folderName)) },
                        { _isNameTaken.postValue(FolderNameTakenEvent.Error()) }
                    )
                    .addTo(bag)
            }
        } ?: run {
            _isNameTaken.postValue(FolderNameTakenEvent.Error())
        }
    }
}

sealed class CreateFolderEvent : Event() {
    class Error : CreateFolderEvent()
    class Success : CreateFolderEvent()
}

sealed class FolderNameTakenEvent : Event() {
    class Success(val taken: Boolean, val folderName: String) : FolderNameTakenEvent()
    class Error : FolderNameTakenEvent()
}