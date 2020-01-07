package com.pixelart.notedock.fragment.note


import android.os.Bundle
import android.view.*
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteNoteDialog
import com.pixelart.notedock.dialog.NoteDialogDeleteSuccessListener
import com.pixelart.notedock.domain.livedata.observer.DataEventObserver
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.NoteDeletedEvent
import com.pixelart.notedock.viewModel.NoteFragmentViewModel
import com.pixelart.notedock.viewModel.SaveNoteEvent
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class NoteFragment : Fragment() {

    private val noteFragmentViewModel: NoteFragmentViewModel by viewModel()

    private val args: NoteFragmentArgs by navArgs()
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
        setHasOptionsMenu(true)
        noteFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClosingOfKeyboard()
        setupToolbar()

    }

    private fun setupToolbar() {
        view?.toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteNote -> {
                    createDeleteNoteDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClosingOfKeyboard() {
        context?.let { context ->
            editTextNoteTitle.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    hideSoftKeyboard(context, view)
                }
            }
            editTextNoteDescription.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    hideSoftKeyboard(context, view)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    override fun onPause() {
        super.onPause()

        if (!deletingNote) {
            saveNote()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteNote -> createDeleteNoteDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeLiveData() {
        //Cez onStartStop
        noteFragmentViewModel.loadNote(args.folderUUID, args.noteUUID)

        noteFragmentViewModel.deleteNoteButtonClicked.observe(
            this,
            SpecificEventObserver<ButtonPressedEvent> {
                createDeleteNoteDialog()
            })

        noteFragmentViewModel.onBackClicked.observe(this, EventObserver {
            findNavController().popBackStack()
        })

        noteFragmentViewModel.noteDeleted.observe(this, Observer { event ->
            view?.let { view ->
                when (event) {
                    is NoteDeletedEvent.Success -> {
                        deletingNote = true
                        view.findNavController().popBackStack()
                    }
                    is NoteDeletedEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })

        noteFragmentViewModel.noteSaved.observe(this, Observer { event ->
            view?.let { view ->
                when (event) {
                    is SaveNoteEvent.Success -> {
                        view.findNavController().popBackStack()
                    }
                    is SaveNoteEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })
    }

    private fun createDeleteNoteDialog() {
        fragmentManager?.let { fragmentManager ->
            val dialog = DeleteNoteDialog(object : NoteDialogDeleteSuccessListener {
                override fun onDelete() {
                    noteFragmentViewModel.deleteNote(args.folderUUID, args.noteUUID)
                }
            })
            dialog.show(fragmentManager, "Delete note dialog")
        }
    }

    private fun saveNote() {
        val note = NoteModel()
        note.uuid = args.noteUUID
        note.noteTitle = editTextNoteTitle.text.toString()
        note.noteDescription = editTextNoteDescription.text.toString()
        noteFragmentViewModel.saveNote(args.folderUUID, note)
    }
}
