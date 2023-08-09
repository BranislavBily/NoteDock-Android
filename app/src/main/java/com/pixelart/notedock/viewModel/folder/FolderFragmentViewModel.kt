package com.pixelart.notedock.viewModel.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.note.CreateNoteUseCase
import com.pixelart.notedock.domain.usecase.note.DeleteNoteUseCase
import com.pixelart.notedock.domain.usecase.note.MarkNoteUseCase
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.note.GenericCRUDEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val folderUUID: String,
    private val folderName: String,
    private val auth: FirebaseAuth,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val createFolderUseCase: CreateNoteUseCase,
    private val markNoteUseCase: MarkNoteUseCase,
    private val notesRepository: NotesRepository,
) : LifecycleViewModel() {

    val toolbarTitle: LiveData<String> = MutableLiveData<String>().apply { postValue(folderName) }

    private val _loadedNotes = MutableLiveData<LoadNotesEvent>()
    val loadedNotes: LiveData<LoadNotesEvent> = _loadedNotes

    private val _onBackClicked = MutableLiveData<ButtonPressedEvent>()
    val onBackClicked: LiveData<ButtonPressedEvent> = _onBackClicked

    private val _deleteNote = MutableLiveData<GenericCRUDEvent>()
    val deleteNote: LiveData<GenericCRUDEvent> = _deleteNote

    private val _fabClicked = MutableLiveData<ButtonPressedEvent>()
    val fabClicked: LiveData<ButtonPressedEvent> = _fabClicked

    private val _noteCreated = MutableLiveData<CreateNoteEvent>()
    val noteCreated: LiveData<CreateNoteEvent> = _noteCreated

    private val _markNote = MutableLiveData<GenericCRUDEvent>()
    val markNote: LiveData<GenericCRUDEvent> = _markNote

    override fun onStartStopObserve(disposeBag: CompositeDisposable) {
        super.onStartStopObserve(disposeBag)

        loadNotes(disposeBag)
    }

    fun onBackClicked() {
        _onBackClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun onFABClicked() {
        _fabClicked.postValue(ButtonPressedEvent.Pressed())
    }

    fun createNote(folderUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                createFolderUseCase.createNote(user, folderUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _noteCreated.postValue(CreateNoteEvent.Success(it)) },
                        { error ->
                            _noteCreated.postValue(CreateNoteEvent.Error())
                        },
                    )
                    .addTo(bag)
            }
        } ?: run {
            _noteCreated.postValue(CreateNoteEvent.NoUserFound())
        }
    }

    fun markNote(folderUUID: String, note: NoteModel) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                markNoteUseCase.markNote(user, folderUUID, note)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _markNote.postValue(GenericCRUDEvent.Success()) },
                        { error ->
                            _markNote.postValue(GenericCRUDEvent.Error())
                        },
                    )
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
                    .subscribe(
                        { _deleteNote.postValue(GenericCRUDEvent.Success()) },
                        { error ->
                            _deleteNote.postValue(GenericCRUDEvent.Error())
                        },
                    )
                    .addTo(bag)
            }
        } ?: run {
            _deleteNote.postValue(GenericCRUDEvent.NoUserFound())
        }
    }

    private fun separateNotes(notes: ArrayList<NoteModel>) {
        val markedNotes = ArrayList<NoteModel>()
        val unmarkedNotes = ArrayList<NoteModel>()
        for (note in notes) {
            note.marked?.let { marked ->
                if (marked) {
                    markedNotes.add(note)
                } else {
                    unmarkedNotes.add(note)
                }
            }
        }
        _loadedNotes.postValue(LoadNotesEvent.Success(markedNotes, unmarkedNotes))
    }

    private fun loadNotes(disposeBag: CompositeDisposable) {
        auth.currentUser?.let { user ->
            notesRepository.getNotes(user, folderUUID)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { separateNotes(it) },
                    { error ->
                        _loadedNotes.postValue(LoadNotesEvent.Error())
                    },
                )
                .addTo(disposeBag)
        } ?: run {
            _loadedNotes.postValue(LoadNotesEvent.NoUserFoundError())
        }
    }
}

sealed class CreateNoteEvent : Event() {
    class Success(val noteUUID: String) : CreateNoteEvent()
    class Error : CreateNoteEvent()
    class NoUserFound : CreateNoteEvent()
}

sealed class LoadNotesEvent : Event() {
    class Success(val markedNotes: ArrayList<NoteModel>, val unmarkedNotes: ArrayList<NoteModel>) :
        LoadNotesEvent()

    class Error : LoadNotesEvent()
    class NoUserFoundError : LoadNotesEvent()
}
