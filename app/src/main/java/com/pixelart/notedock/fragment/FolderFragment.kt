package com.pixelart.notedock.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.NotesAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteFolderDialog
import com.pixelart.notedock.dialog.FolderDialogDeleteSuccessListener
import com.pixelart.notedock.viewModel.*
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.viewmodel.ext.android.viewModel


class FolderFragment : Fragment(), NotesAdapter.OnNoteClickListener {

    private val folderFragmentViewModel: FolderFragmentViewModel by viewModel()

    val args: FolderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folder,
            BR.viewmodel to folderFragmentViewModel
        )
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
        folderFragmentViewModel.loadNotes(args.folderUUID)

        folderFragmentViewModel.folderButtonClicked.observe(this, Observer { event ->
            when(event) {
                DeleteFolderButtonEvent.OnClick -> createDeleteDialog()
            }
        })

        folderFragmentViewModel.folderDeleted.observe(this, Observer { event ->
            when(event) {
                is FolderDeleteEvent.Success ->  {
                    view?.findNavController()?.popBackStack()
                }
                is FolderDeleteEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        folderFragmentViewModel.loadedNotes.observe(this, Observer { notes ->
            notesAdapter.setNewData(notes)
        })

        folderFragmentViewModel.fabClicked.observe(this, Observer { event ->
            when(event) {
                FABButtonEvent.Clicked -> createNote()
            }
        })

        folderFragmentViewModel.noteCreated.observe(this, Observer { event ->
            when(event) {
                is CreateNoteEvent.Success -> navigateToNote(event.noteUUID)
                is CreateNoteEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
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
        val action = FolderFragmentDirections.actionFolderFragmentToNoteFragment(args.folderUUID + " " + noteUUID)
        val navigationRouter = NavigationRouter(view)
        navigationRouter.openAction(action)
    }

    override fun onNoteClick(noteUUID: String?) {
        noteUUID?.let {
            val action = FolderFragmentDirections.actionFolderFragmentToNoteFragment(args.folderUUID + " " + noteUUID)
            val navigationRouter = NavigationRouter(view)
            navigationRouter.openAction(action)
        }
    }
}