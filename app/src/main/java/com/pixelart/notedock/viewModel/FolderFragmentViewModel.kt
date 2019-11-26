package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.usecase.DeleteFolderUseCase
import com.pixelart.notedock.domain.usecase.LoadNotesUseCase
import com.pixelart.notedock.model.NoteModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val loadNotesUseCase: LoadNotesUseCase
): LifecycleViewModel() {


    private val _loadedNotes = MutableLiveData<ArrayList<NoteModel>>()
    val loadedNotes: LiveData<ArrayList<NoteModel>> = _loadedNotes

    private val _buttonClicked = SingleLiveEvent<DeleteButtonEvent>()
    val buttonClicked: LiveData<DeleteButtonEvent> = _buttonClicked

    private val _folderDeleted = SingleLiveEvent<FolderDeleteEvent>()
    val folderDeleted: LiveData<FolderDeleteEvent> = _folderDeleted

    fun onDeleteButtonClicked() {
        _buttonClicked.postValue(DeleteButtonEvent.OnClick)
    }

    fun deleteFolderModel(folderUUID: String) {
        startStopDisposeBag?.let { bag ->
            deleteFolderUseCase.deleteFolder(folderUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { _folderDeleted.postValue(FolderDeleteEvent.Success) },
                    { _folderDeleted.postValue(FolderDeleteEvent.Error) }
                )
                .addTo(bag)
        }
    }

    fun loadNotes(folderUUID: String) {
        loadNotesUseCase.loadNotes(folderUUID, EventListener { notes, _ ->
            _loadedNotes.postValue(notes)
        })
    }
}

sealed class FolderDeleteEvent {
    object Error: FolderDeleteEvent()
    object Success: FolderDeleteEvent()
}

sealed class DeleteButtonEvent {
    object OnClick: DeleteButtonEvent()
}