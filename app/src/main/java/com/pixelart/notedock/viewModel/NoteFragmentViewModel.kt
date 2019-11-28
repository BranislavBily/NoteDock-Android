package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.repository.NotesRepository
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class NoteFragmentViewModel(private val notesRepository: NotesRepository): LifecycleViewModel() {

    private val _textViewNoteTitle = MutableLiveData<String>()
    val textViewNoteTitle: LiveData<String> = _textViewNoteTitle

    private val _textViewNoteDescription = MutableLiveData<String>()
    val textViewNoteDescription: LiveData<String> = _textViewNoteDescription

    fun loadNotes(folderUUID: String, noteUUID: String) {
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
}