package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.note.DeleteNoteUseCase
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class NoteFragmentViewModel(private val notesRepository: NotesRepository,
                            private val deleteNoteUseCase: DeleteNoteUseCase): LifecycleViewModel() {

    private val _textViewNoteTitle = MutableLiveData<String>()
    val textViewNoteTitle: LiveData<String> = _textViewNoteTitle

    private val _textViewNoteDescription = MutableLiveData<String>()
    val textViewNoteDescription: LiveData<String> = _textViewNoteDescription

    private val _deleteButtonClicked = SingleLiveEvent<DeleteNoteButtonClickEvent>()
    val deleteNoteButtonClicked: LiveData<DeleteNoteButtonClickEvent> = _deleteButtonClicked

    private val _noteDeleted = SingleLiveEvent<NoteDeletedEvent>()
    val noteDeleted: LiveData<NoteDeletedEvent> = _noteDeleted

    fun loadNote(folderUUID: String, noteUUID: String) {
        startStopDisposeBag?.let { bag ->
            notesRepository.loadNote(folderUUID, noteUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe( { noteModel ->
                    _textViewNoteTitle.postValue(noteModel.noteTitle)
                    _textViewNoteDescription.postValue(noteModel.noteDescription)
                }, {})
                .addTo(bag)
        }
    }

    fun deleteNote(folderUUID: String, noteUUID: String) {
        startStopDisposeBag?.let { bag ->
            deleteNoteUseCase.deleteNote(folderUUID, noteUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe( { _noteDeleted.postValue(NoteDeletedEvent.Success)},
                            { _noteDeleted.postValue(NoteDeletedEvent.Error)})
        }
    }

    fun onButtonDeleteNoteClick() {
        _deleteButtonClicked.postValue(DeleteNoteButtonClickEvent.Clicked)
    }

    fun onButtonSaveNoteClick() {
        _saveButtonClicked.postValue(SaveNoteButtonClickEvent.Clicked)
    }
}

sealed class NoteDeletedEvent {
    object Success: NoteDeletedEvent()
    object Error: NoteDeletedEvent()
}

sealed class DeleteNoteButtonClickEvent {
    object Clicked: DeleteNoteButtonClickEvent()
}

sealed class SaveNoteButtonClickEvent {
    object Clicked: SaveNoteButtonClickEvent()
}