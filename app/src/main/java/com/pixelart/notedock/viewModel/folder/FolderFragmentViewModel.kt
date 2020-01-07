package com.pixelart.notedock.viewModel.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.livedata.model.Event
import com.pixelart.notedock.domain.repository.NotesRepository
import com.pixelart.notedock.domain.usecase.folder.DeleteFolderUseCase
import com.pixelart.notedock.domain.usecase.note.CreateNoteUseCase
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val folderUUID: String,
    private val auth: FirebaseAuth,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val createFolderUseCase: CreateNoteUseCase,
    private val notesRepository: NotesRepository
): LifecycleViewModel() {


    private val _loadedNotes = MutableLiveData<ArrayList<NoteModel>>()
    val loadedNotes: LiveData<ArrayList<NoteModel>> = _loadedNotes

    private val _buttonClicked = MutableLiveData<ButtonPressedEvent>()
    val folderButtonClicked: LiveData<ButtonPressedEvent> = _buttonClicked

    private val _folderDeleted = MutableLiveData<FolderDeleteEvent>()
    val folderDeleted: LiveData<FolderDeleteEvent> = _folderDeleted

    private val _fabClicked = MutableLiveData<ButtonPressedEvent>()
    val fabClicked: LiveData<ButtonPressedEvent> = _fabClicked

    private val _noteCreated = MutableLiveData<CreateNoteEvent>()
    val noteCreated: LiveData<CreateNoteEvent> = _noteCreated

    override fun onStartStopObserve(disposeBag: CompositeDisposable) {
        super.onStartStopObserve(disposeBag)

        loadNotes(disposeBag)
    }

    fun onDeleteFolderButtonClicked() {
        _buttonClicked.postValue(ButtonPressedEvent.Pressed())
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
                    .subscribe( {_noteCreated.postValue(CreateNoteEvent.Success(it))},
                                { _noteCreated.postValue(CreateNoteEvent.Error()) }
                    )
                    .addTo(bag)
            }
        }
    }

    fun deleteFolderModel(folderUUID: String) {
        auth.currentUser?.let { user ->
            startStopDisposeBag?.let { bag ->
                deleteFolderUseCase.deleteFolder(user, folderUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        { _folderDeleted.postValue(FolderDeleteEvent.Success()) },
                        { _folderDeleted.postValue(FolderDeleteEvent.Error()) }
                    )
                    .addTo(bag)
            }
        }
    }

    private fun loadNotes(disposeBag: CompositeDisposable) {
        auth.currentUser?.let { user ->
            notesRepository.getNotes(user, folderUUID)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({ _loadedNotes.postValue(it) }, {})
                .addTo(disposeBag)
            }
    }
}

sealed class CreateNoteEvent: Event() {
    class Success(val noteUUID: String): CreateNoteEvent()
    class Error: CreateNoteEvent()
}

sealed class FolderDeleteEvent: Event() {
    class Error: FolderDeleteEvent()
    class Success: FolderDeleteEvent()
}