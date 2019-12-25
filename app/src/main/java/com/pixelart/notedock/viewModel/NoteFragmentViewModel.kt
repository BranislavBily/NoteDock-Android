package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.note.DeleteNoteUseCase
import com.pixelart.notedock.domain.usecase.note.UpdateNoteUseCase
import com.pixelart.notedock.model.NoteModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class NoteFragmentViewModel(private val notesRepository: NotesRepository,
                            private val deleteNoteUseCase: DeleteNoteUseCase,
                            private val updateNoteUseCase: UpdateNoteUseCase): LifecycleViewModel() {

    private val _editTextNoteTitle = MutableLiveData<String>()
    val editTextNoteTitle: LiveData<String> = _editTextNoteTitle

    private val _editTextNoteDescription = MutableLiveData<String>()
    val editTextNoteDescription: LiveData<String> = _editTextNoteDescription

    private val _deleteButtonClicked = SingleLiveEvent<DeleteNoteButtonClickEvent>()
    val deleteNoteButtonClicked: LiveData<DeleteNoteButtonClickEvent> = _deleteButtonClicked

    private val _noteDeleted = SingleLiveEvent<NoteDeletedEvent>()
    val noteDeleted: LiveData<NoteDeletedEvent> = _noteDeleted

    private val _noteSaved = SingleLiveEvent<SaveNoteEvent>()
    val noteSaved: LiveData<SaveNoteEvent> = _noteSaved

    fun loadNote(folderUUID: String, noteUUID: String) {
        startStopDisposeBag?.let { bag ->
            notesRepository.loadNote(folderUUID, noteUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe( { noteModel ->
                    _editTextNoteTitle.postValue(noteModel.noteTitle)
                    _editTextNoteDescription.postValue(noteModel.noteDescription)
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
                .addTo(bag)
        }
    }

    fun saveNote(folderUUID: String, noteModel: NoteModel) {
        startStopDisposeBag?.let { bag ->
            updateNoteUseCase.updateNote(folderUUID, noteModel)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ _noteSaved.postValue(SaveNoteEvent.Success)},
                    { _noteSaved.postValue(SaveNoteEvent.Error)})
                .addTo(bag)
        }
    }

    fun onButtonDeleteNoteClick() {
        _deleteButtonClicked.postValue(DeleteNoteButtonClickEvent.Clicked)
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

sealed class SaveNoteEvent {
    object Success: SaveNoteEvent()
    object Error: SaveNoteEvent()
}