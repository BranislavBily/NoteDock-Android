package com.pixelart.notedock.fragment.note


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.note.GenericCRUDEvent
import com.pixelart.notedock.viewModel.note.LoadNoteEvent
import com.pixelart.notedock.viewModel.note.NoteFragmentViewModel
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteFragment : Fragment() {

    private val noteFragmentViewModel: NoteFragmentViewModel by viewModel {
        parametersOf(args.folderUUID, args.noteUUID)
    }

    private val args: NoteFragmentArgs by navArgs()

    //Please change this later
    private var deletingNote = false

    private var note: NoteModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        setupToolbar()
        setupClosingOfKeyboard()
        observeLiveData()
    }

    override fun onResume() {
        super.onResume()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            user.reload()
                .addOnFailureListener { error ->
                    //All is well
                    if (error !is FirebaseNetworkException) {
                        openLoginActivity()
                    }
                }
        }
    }

    override fun onPause() {
        super.onPause()

        if (!deletingNote) {
            saveNote()
        }
    }

    private fun setupToolbar() {
        view?.toolbar?.menu?.getItem(1)?.isVisible = false
        view?.toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteNote -> {
                    noteFragmentViewModel.deleteNote(args.folderUUID, args.noteUUID)
                    true
                }
                R.id.doneNote -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClosingOfKeyboard() {
        view?.let { parentView ->
            context?.let { context ->
                editTextNoteTitle.setOnFocusChangeListener { view, hasFocus ->
                    parentView.toolbar?.menu?.getItem(1)?.isVisible = true
                    if (!hasFocus) {
                        hideSoftKeyboard(context, view)
                    }
                }
                editTextNoteDescription.setOnFocusChangeListener { view, hasFocus ->
                    parentView.toolbar?.menu?.getItem(1)?.isVisible = true
                    if (!hasFocus) {
                        hideSoftKeyboard(context, view)
                    }
                }
            }
        }
    }

    private fun observeLiveData() {

        noteFragmentViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        noteFragmentViewModel.noteDeleted.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when (event) {
                    is GenericCRUDEvent.Success -> {
                        deletingNote = true
                        view.findNavController().popBackStack()
                    }
                    is GenericCRUDEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is GenericCRUDEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                }
            }
        })

        noteFragmentViewModel.noteSaved.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when (event) {
                    is GenericCRUDEvent.Success -> {
                    }
                    is GenericCRUDEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is GenericCRUDEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)//Go to login somehow
                }
            }
        })

        noteFragmentViewModel.noteLoad.observe(
            viewLifecycleOwner,
            SpecificEventObserver<LoadNoteEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is LoadNoteEvent.Success -> this.note = event.note
                        is LoadNoteEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                        is LoadNoteEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                    }
                }
            })
    }

    private fun saveNote() {
        //Hide keyboard
        context?.let { context ->
            view?.let { view ->
                hideSoftKeyboard(context, view)
                view.requestFocus()
                //Hides done menu item
                toolbar?.menu?.getItem(1)?.isVisible = false
            }
        }

        //Get note values
        val note = NoteModel()
        note.uuid = args.noteUUID
        note.noteTitle = editTextNoteTitle.text.toString()
        note.noteDescription = editTextNoteDescription.text.toString()

        //If change occurred, save
        if (!this.note?.noteTitle.equals(note.noteTitle) || !this.note?.noteDescription.equals(note.noteDescription)) {
            noteFragmentViewModel.saveNote(args.folderUUID, note)
        }
    }
}
