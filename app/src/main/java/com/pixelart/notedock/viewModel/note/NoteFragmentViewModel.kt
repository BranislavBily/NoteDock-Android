package com.pixelart.notedock.viewModel.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.note.DeleteNoteUseCase
import com.pixelart.notedock.domain.usecase.note.UpdateNoteUseCase
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class NoteFragmentViewModel(
    private val folderUUID: String,
    private val noteUUID: String,
    private val notesRepository: NotesRepository,
    private val auth: FirebaseAuth,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
) : LifecycleViewModel() {

    private val _editTextNoteTitle = MutableLiveData<String>()
    val editTextNoteTitle: LiveData<String> = _editTextNoteTitle

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _editTextNoteDescription = MutableLiveData<String>()
    val editTextNoteDescription: LiveData<String> = _editTextNoteDescription

    private val _noteDeleted = MutableLiveData<GenericCRUDEvent>()
    val noteDeleted: LiveData<GenericCRUDEvent> = _noteDeleted

    private val _noteLoad = MutableLiveData<LoadNoteEvent>()
    val noteLoad: LiveData<LoadNoteEvent> = _noteLoad

    private val _noteSaved = MutableLiveData<GenericCRUDEvent>()
    val noteSaved: LiveData<GenericCRUDEvent> = _noteSaved

    override fun onStartStopObserve(disposeBag: CompositeDisposable) {
        super.onStartStopObserve(disposeBag)

        loadNote(disposeBag)
    }

    fun onBackPressed() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    private fun loadNote(disposeBag: CompositeDisposable) {
        auth.currentUser?.let { user ->
            notesRepository.loadNote(user, folderUUID, noteUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ noteModel ->
                    _editTextNoteTitle.postValue(noteModel.noteTitle)
                    _editTextNoteDescription.postValue(noteModel.noteDescription)
                    _noteLoad.postValue(LoadNoteEvent.Success(noteModel))
                }, { error ->
                    _noteLoad.postValue(LoadNoteEvent.Error())
                })
                .addTo(disposeBag)
        } ?: run {
            _noteLoad.postValue(LoadNoteEvent.NoUserFound())
        }
    }

    fun deleteNote(folderUUID: String, noteUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                deleteNoteUseCase.deleteNote(user, folderUUID, noteUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _noteDeleted.postValue(GenericCRUDEvent.Success()) },
                        { error ->
                            _noteDeleted.postValue(GenericCRUDEvent.Error())
                        },
                    )
                    .addTo(bag)
            }
        } ?: run {
            _noteDeleted.postValue(GenericCRUDEvent.NoUserFound())
        }
    }

    fun saveNote(folderUUID: String, noteModel: NoteModel) {
        auth.currentUser?.let { user ->
            updateNoteUseCase.updateNote(user, folderUUID, noteModel)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { _noteSaved.postValue(GenericCRUDEvent.Success()) },
                    { error ->
                        _noteSaved.postValue(GenericCRUDEvent.Error())
                    },
                )
        } ?: run {
            _noteSaved.postValue(GenericCRUDEvent.NoUserFound())
        }
    }
}

sealed class LoadNoteEvent : Event() {
    class Success(val note: NoteModel) : LoadNoteEvent()
    class Error : LoadNoteEvent()
    class NoUserFound : LoadNoteEvent()
}

sealed class GenericCRUDEvent : Event() {
    class Success : GenericCRUDEvent()
    class Error : GenericCRUDEvent()
    class NoUserFound : GenericCRUDEvent()
}
