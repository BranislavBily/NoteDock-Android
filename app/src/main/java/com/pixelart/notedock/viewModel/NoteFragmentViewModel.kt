package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.DataEvent
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.note.DeleteNoteUseCase
import com.pixelart.notedock.domain.usecase.note.UpdateNoteUseCase
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*

class NoteFragmentViewModel(
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : LifecycleViewModel() {

    private val _editTextNoteTitle = MutableLiveData<String>()
    val editTextNoteTitle: LiveData<String> = _editTextNoteTitle

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _editTextNoteDescription = MutableLiveData<String>()
    val editTextNoteDescription: LiveData<String> = _editTextNoteDescription

    private val _deleteButtonClicked = MutableLiveData<ButtonPressedEvent>()
    val deleteNoteButtonClicked: LiveData<ButtonPressedEvent> = _deleteButtonClicked

    private val _noteDeleted = MutableLiveData<NoteDeletedEvent>()
    val noteDeleted: LiveData<NoteDeletedEvent> = _noteDeleted

    private val _noteSaved = MutableLiveData<SaveNoteEvent>()
    val noteSaved: LiveData<SaveNoteEvent> = _noteSaved

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun loadNote(folderUUID: String, noteUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                notesRepository.loadNote(user, folderUUID, noteUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({ noteModel ->
                        _editTextNoteTitle.postValue(noteModel.noteTitle)
                        _editTextNoteDescription.postValue(noteModel.noteDescription)
                    }, {})
                    .addTo(bag)
            }
        }
    }

    fun deleteNote(folderUUID: String, noteUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                deleteNoteUseCase.deleteNote(user, folderUUID, noteUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({ _noteDeleted.postValue(NoteDeletedEvent.Success()) },
                        { _noteDeleted.postValue(NoteDeletedEvent.Error()) })
                    .addTo(bag)
            }
        }

    }

    fun saveNote(folderUUID: String, noteModel: NoteModel) {
        auth.currentUser?.let { user ->
            updateNoteUseCase.updateNote(user, folderUUID, noteModel)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ _noteSaved.postValue(SaveNoteEvent.Success()) },
                           { _noteSaved.postValue(SaveNoteEvent.Error()) })
        }
    }

    fun onButtonDeleteNoteClick() {
        _deleteButtonClicked.postValue(ButtonPressedEvent.Pressed())
    }
}

sealed class NoteDeletedEvent : Event() {
    class Success : NoteDeletedEvent()
    class Error : NoteDeletedEvent()
}

sealed class SaveNoteEvent : Event() {
    class Success : SaveNoteEvent()
    class Error : SaveNoteEvent()
}