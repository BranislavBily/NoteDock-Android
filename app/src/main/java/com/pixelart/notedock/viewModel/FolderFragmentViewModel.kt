package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.folder.DeleteFolderUseCase
import com.pixelart.notedock.model.NoteModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val notesRepository: NotesRepository
): LifecycleViewModel() {


    private val _loadedNotes = MutableLiveData<ArrayList<NoteModel>>()
    val loadedNotes: LiveData<ArrayList<NoteModel>> = _loadedNotes

    private val _buttonClicked = SingleLiveEvent<DeleteFolderButtonEvent>()
    val folderButtonClicked: LiveData<DeleteFolderButtonEvent> = _buttonClicked

    private val _folderDeleted = SingleLiveEvent<FolderDeleteEvent>()
    val folderDeleted: LiveData<FolderDeleteEvent> = _folderDeleted

    private val _fabClicked = SingleLiveEvent<FABButtonEvent>()
    val fabClicked: LiveData<FABButtonEvent> = _fabClicked

    fun onDeleteFolderButtonClicked() {
        _buttonClicked.postValue(DeleteFolderButtonEvent.OnClick)
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
        notesRepository.loadNotes(folderUUID, EventListener { notes, _ ->
            _loadedNotes.postValue(notes)
        })
    }

    fun onFABClicked() {
        _fabClicked.postValue(FABButtonEvent.Clicked)
    }
}

sealed class FABButtonEvent {
    object Clicked: FABButtonEvent()
}

sealed class FolderDeleteEvent {
    object Error: FolderDeleteEvent()
    object Success: FolderDeleteEvent()
}

sealed class DeleteFolderButtonEvent {
    object OnClick: DeleteFolderButtonEvent()
}