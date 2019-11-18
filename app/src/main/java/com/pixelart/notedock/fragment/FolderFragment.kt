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
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteFolderDialog
import com.pixelart.notedock.dialog.FolderDialogDeleteSuccessListener
import com.pixelart.notedock.domain.repository.FolderRepository
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.DeleteButtonEvent
import com.pixelart.notedock.viewModel.FolderDeleteEvent
import com.pixelart.notedock.viewModel.FolderFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FolderFragment : Fragment() {

    private val folderRepository: FolderRepository by inject()
    private val folderFragmentViewModel: FolderFragmentViewModel by viewModel()

    private var folderModel = FolderModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folder,
            BR.viewmodel to folderFragmentViewModel
        )
        folderFragmentViewModel.lifecycleOwner = this
        arguments?.let { uidOfFolder ->
            val uid = uidOfFolder.getString("uid")
            uid?.let {
                folderRepository.getFolder(uid, EventListener { folder, _ ->
                    folder?.let {
                        folderModel = it
                        textViewFolderDescriptionUID.text = it.uid
                        textViewFolderDescriptionName.text = it.name
                        textViewFolderDescriptionNotesCount.text = it.notesCount
                        activity?.title = it.name
                    }
                })
            }
        }
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {
        folderFragmentViewModel.buttonClicked.observe(this, Observer {event ->
            when(event) {
                DeleteButtonEvent.onClick -> createDeleteDialog()
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
    }

    private fun createDeleteDialog() {
        fragmentManager?.let { fragmentManager ->
            val deleteFolderDialog = DeleteFolderDialog(object : FolderDialogDeleteSuccessListener {
                    override fun onDelete() {
                        folderFragmentViewModel.deleteFolderModel(folderModel)
                    }
                })
            deleteFolderDialog.show(fragmentManager, "Delete Folder Dialog")
        }
    }
}