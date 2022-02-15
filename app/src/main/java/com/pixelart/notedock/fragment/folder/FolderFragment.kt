package com.pixelart.notedock.fragment.folder


import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.note.NotesAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.databinding.NoteOptionsLayoutBinding
import com.pixelart.notedock.dialog.DeleteFolderDialog
import com.pixelart.notedock.dialog.FolderDialogDeleteSuccessListener
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.fragment.note.NoteOptionsFragment
import com.pixelart.notedock.fragment.note.OnNoteOptionsClickListener
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.folder.*
import com.pixelart.notedock.viewModel.note.GenericCRUDEvent
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FolderFragment : Fragment(),
    NotesAdapter.OnNoteClickListener,
    NotesAdapter.OnImageClickListener,
    OnNoteOptionsClickListener {
    private val args: FolderFragmentArgs by navArgs()
    private val folderFragmentViewModel: FolderFragmentViewModel by viewModel {
        parametersOf(args.folderUUID, args.folderName)
    }

    private var markedNotesAdapter: NotesAdapter? = null
    private var unMarkedNotesAdapter: NotesAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folder,
            BR.viewmodel to folderFragmentViewModel
        )
        setHasOptionsMenu(true)
        folderFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markedNotesAdapter = NotesAdapter(this, this)
        unMarkedNotesAdapter = NotesAdapter(this, this)

        setupRecyclerView()
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

    private fun setupRecyclerView() {
        recyclerViewMarkedNotes.layoutManager = LinearLayoutManager(context)
        recyclerViewMarkedNotes.adapter = markedNotesAdapter
        recyclerViewMarkedNotes.isNestedScrollingEnabled = false
        recyclerViewUnMarkedNotes.layoutManager = LinearLayoutManager(context)
        recyclerViewUnMarkedNotes.adapter = unMarkedNotesAdapter
        recyclerViewUnMarkedNotes.isNestedScrollingEnabled = false
    }

    private fun observeLiveData() {

        folderFragmentViewModel.loadedNotes.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when (event) {
                    is LoadNotesEvent.Success -> {
                        markedNotesAdapter?.setNewData(event.markedNotes)
                        unMarkedNotesAdapter?.setNewData(event.unmarkedNotes)
                    }
                    is LoadNotesEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is LoadNotesEvent.NoUserFoundError -> R.string.no_user_found.showAsSnackBar(view)
                }
            }
        })

        folderFragmentViewModel.fabClicked.observe(
            viewLifecycleOwner,
            SpecificEventObserver<ButtonPressedEvent> {
                createNote()
            })

        folderFragmentViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        folderFragmentViewModel.markNote.observe(
            viewLifecycleOwner,
            SpecificEventObserver<GenericCRUDEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is GenericCRUDEvent.Success -> {

                        }
                        is GenericCRUDEvent.Error -> R.string.error_occurred.showAsSnackBar(
                            view
                        )
                        is GenericCRUDEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(
                            view
                        )
                    }
                }
            })

        folderFragmentViewModel.noteCreated.observe(
            viewLifecycleOwner,
            SpecificEventObserver<CreateNoteEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is CreateNoteEvent.Success -> NavigationRouter(view).folderToNote(
                            args.folderUUID,
                            event.noteUUID
                        )
                        is CreateNoteEvent.Error -> {
                            R.string.error_occurred.showAsSnackBar(view)
                        }
                        is CreateNoteEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                    }
                } ?: run {
                    Log.e("FolderFragment", "View not found")
                }
            })

        folderFragmentViewModel.deleteNote.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when (event) {
                    is GenericCRUDEvent.Success -> {
                    }
                    is GenericCRUDEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is GenericCRUDEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                }
            }
        })
    }

    private fun createNote() {
        folderFragmentViewModel.createNote(args.folderUUID)
    }

    override fun onNoteClick(noteUUID: String?) {
        noteUUID?.let { NavigationRouter(view).folderToNote(args.folderUUID, it) }
    }

    override fun onLongNoteClick(noteUUID: String?) {
        noteUUID?.let { uuid ->
            val optionsFragment = NoteOptionsFragment(uuid, this)
            optionsFragment.show(parentFragmentManager, "NoteOptionsFragment")
        }
    }

    override fun onImageClick(note: NoteModel) {
        folderFragmentViewModel.markNote(args.folderUUID, note)
    }

    override fun onOptionClick(noteUUID: String, options: Options) {
        folderFragmentViewModel.deleteNote(args.folderUUID, noteUUID)
    }
}