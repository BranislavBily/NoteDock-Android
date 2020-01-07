package com.pixelart.notedock.fragment.folder


import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.pixelart.notedock.adapter.NotesAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteFolderDialog
import com.pixelart.notedock.dialog.FolderDialogDeleteSuccessListener
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.folder.CreateNoteEvent
import com.pixelart.notedock.viewModel.folder.FolderDeleteEvent
import com.pixelart.notedock.viewModel.folder.FolderFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FolderFragment : Fragment(), NotesAdapter.OnNoteClickListener {
    private val args: FolderFragmentArgs by navArgs()
    private val folderFragmentViewModel: FolderFragmentViewModel by viewModel {
        parametersOf(args.folderUUID, args.folderName)
    }


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

        val notesAdapter = NotesAdapter(this)
        setupRecyclerView(notesAdapter)
        observeLiveData(notesAdapter)
    }

    private fun setupRecyclerView(notesAdapter: NotesAdapter) {
        recyclerViewNotes.layoutManager = LinearLayoutManager(context)
        recyclerViewNotes.adapter = notesAdapter
    }

    private fun observeLiveData(notesAdapter: NotesAdapter) {

        folderFragmentViewModel.folderButtonClicked.observe(
            this,
            SpecificEventObserver<ButtonPressedEvent> {
                createDeleteDialog()
            })

        folderFragmentViewModel.folderDeleted.observe(this, Observer { event ->
            view?.let { view ->
                when (event) {
                    is FolderDeleteEvent.Success -> {
                        view.findNavController().popBackStack()
                    }
                    is FolderDeleteEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                }
            } ?: run {
                Log.e("FolderFragment", "View not found")
            }
        })

        folderFragmentViewModel.loadedNotes.observe(this, Observer { notes ->
            notesAdapter.setNewData(notes)
        })

        folderFragmentViewModel.fabClicked.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            createNote()
        })

        folderFragmentViewModel.onBackClicked.observe(this, EventObserver {
            findNavController().popBackStack()
        })


        folderFragmentViewModel.noteCreated.observe(
            this,
            SpecificEventObserver<CreateNoteEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is CreateNoteEvent.Success -> navigateToNote(event.noteUUID)
                        is CreateNoteEvent.Error -> {
                            R.string.error_occurred.showAsSnackBar(view)
                        }
                    }
                } ?: run {
                    Log.e("FolderFragment", "View not found")
                }
            })
    }

    private fun createDeleteDialog() {
        fragmentManager?.let { fragmentManager ->
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
}