package com.pixelart.notedock.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteNoteDialog
import com.pixelart.notedock.dialog.NoteDialogDeleteSuccessListener
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.koin.android.viewmodel.ext.android.viewModel

class NoteFragment : Fragment() {

    private val noteFragmentViewModel: NoteFragmentViewModel by viewModel()

    private val args: NoteFragmentArgs by navArgs()
    private var folderUUID: String = ""
    private var noteUUID: String = ""
    //Please change this later
    private var deletingNote = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_note,
            BR.viewmodel to noteFragmentViewModel
        )
        noteFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = args.folderUUIDnoteUUID.split(" ")
        folderUUID = arguments[0]
        noteUUID = arguments[1]

        setupClosingOfKeyboard()
    }

    private fun setupClosingOfKeyboard() {
        editTextNoteTitle.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                hideKeyboard(view)
            }
        }
        editTextNoteDescription.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                hideKeyboard(view)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    override fun onPause() {
        super.onPause()

        if(!deletingNote) {
            saveNote()
        }
    }

    private fun observeLiveData() {
        noteFragmentViewModel.loadNote(folderUUID, noteUUID)

        noteFragmentViewModel.deleteNoteButtonClicked.observe(this, Observer { event ->
            when(event) {
                DeleteNoteButtonClickEvent.Clicked -> createDeleteNoteDialog()
            }
        })

        noteFragmentViewModel.noteDeleted.observe(this, Observer { event ->
            when(event) {
                NoteDeletedEvent.Success -> {
                    deletingNote = true
                    view?.findNavController()?.popBackStack()
                }
                NoteDeletedEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        noteFragmentViewModel.noteSaved.observe( this, Observer { event ->
            when(event) {
                SaveNoteEvent.Success -> {
                    view?.findNavController()?.popBackStack()
                }
                SaveNoteEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createDeleteNoteDialog() {
        fragmentManager?.let { fragmentManager ->
            val dialog = DeleteNoteDialog(object : NoteDialogDeleteSuccessListener {
                override fun onDelete() {
                    noteFragmentViewModel.deleteNote(folderUUID, noteUUID)
                }
            })
            dialog.show(fragmentManager, "Delete note dialog")
        }
    }

    private fun saveNote() {
        val note = NoteModel()
        note.uuid = noteUUID
        note.noteTitle = editTextNoteTitle.text.toString()
        note.noteDescription = editTextNoteDescription.text.toString()
        noteFragmentViewModel.saveNote(folderUUID, note)
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
