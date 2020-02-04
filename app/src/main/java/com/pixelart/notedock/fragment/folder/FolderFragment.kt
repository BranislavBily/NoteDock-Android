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
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.MarkedNotesAdapter
import com.pixelart.notedock.adapter.UnMarkedNotesAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteFolderDialog
import com.pixelart.notedock.dialog.FolderDialogDeleteSuccessListener
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.NoteModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.folder.*
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FolderFragment : Fragment(),
    MarkedNotesAdapter.OnNoteClickListener,
    MarkedNotesAdapter.OnImageClickListener,
    UnMarkedNotesAdapter.OnNoteClickListener,
    UnMarkedNotesAdapter.OnImageClickListener {
    private val args: FolderFragmentArgs by navArgs()
    private val folderFragmentViewModel: FolderFragmentViewModel by viewModel {
        parametersOf(args.folderUUID, args.folderName)
    }

    private var markedNotesAdapter: MarkedNotesAdapter? = null
    private var unmarkedNotesAdapter: UnMarkedNotesAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        markedNotesAdapter = MarkedNotesAdapter(this, this)
        unmarkedNotesAdapter = UnMarkedNotesAdapter(this, this)

        setupRecyclerView()
        observeLiveData()
    }

    private fun setupRecyclerView() {
        recyclerViewMarkedNotes.layoutManager = LinearLayoutManager(context)
        recyclerViewMarkedNotes.adapter = markedNotesAdapter
        recyclerViewMarkedNotes.isNestedScrollingEnabled = false
        recyclerViewUnMarkedNotes.layoutManager = LinearLayoutManager(context)
        recyclerViewUnMarkedNotes.adapter = unmarkedNotesAdapter
        recyclerViewUnMarkedNotes.isNestedScrollingEnabled = false
    }

    private fun observeLiveData() {

        folderFragmentViewModel.folderButtonClicked.observe(viewLifecycleOwner, SpecificEventObserver<ButtonPressedEvent> {
                createDeleteDialog()
            })

        folderFragmentViewModel.folderDeleted.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when (event) {
                    is FolderDeleteEvent.Success -> {
                        view.findNavController().popBackStack()
                    }
                    is FolderDeleteEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is FolderDeleteEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                }
            } ?: run {
                Log.e("FolderFragment", "View not found")
            }
        })

        folderFragmentViewModel.loadedNotes.observe(viewLifecycleOwner, Observer { event ->
            view?.let { view ->
                when(event) {
                    is LoadNotesEvent.Success -> {
                        markedNotesAdapter?.setNewData(event.pinnedNotes)
                        unmarkedNotesAdapter?.setNewData(event.unPinnedNotes)
                    }
                    is LoadNotesEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                    is LoadNotesEvent.NoUserFoundError -> R.string.no_user_found.showAsSnackBar(view)
                }
            }
        })

        folderFragmentViewModel.fabClicked.observe(viewLifecycleOwner, SpecificEventObserver<ButtonPressedEvent> {
            createNote()
        })

        folderFragmentViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        folderFragmentViewModel.markNote.observe(viewLifecycleOwner, SpecificEventObserver<MarkNoteEvent> { event ->
            view?.let { view ->
                when(event) {
                    is MarkNoteEvent.Success -> {}
                    is MarkNoteEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                }
            }

        })


        folderFragmentViewModel.noteCreated.observe(viewLifecycleOwner, SpecificEventObserver<CreateNoteEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is CreateNoteEvent.Success -> navigateToNote(event.noteUUID)
                        is CreateNoteEvent.Error -> {
                            R.string.error_occurred.showAsSnackBar(view)
                        }
                        is CreateNoteEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(view)
                    }
                } ?: run {
                    Log.e("FolderFragment", "View not found")
                }
            })
    }

    private fun createDeleteDialog() {
        activity?.supportFragmentManager?.let { fragmentManager ->
            val deleteFolderDialog = DeleteFolderDialog(object : FolderDialogDeleteSuccessListener {
                override fun onDelete() {
                    folderFragmentViewModel.deleteFolderModel(args.folderUUID)
                }
            })
            deleteFolderDialog.show(fragmentManager, "Delete Folder Dialog")
        }
    }

    private fun createNote() {
        folderFragmentViewModel.createNote(args.folderUUID)
    }

    private fun navigateToNote(noteUUID: String) {
        val action =
            FolderFragmentDirections.actionFolderFragmentToNoteFragment(args.folderUUID, noteUUID)
        val navigationRouter = NavigationRouter(view)
        navigationRouter.openAction(action)
    }

    override fun onNoteClick(noteUUID: String?) {
        noteUUID?.let { navigateToNote(it) }
    }

    override fun onImageClick(note: NoteModel) {
        folderFragmentViewModel.markNote(args.folderUUID, note)
    }
}