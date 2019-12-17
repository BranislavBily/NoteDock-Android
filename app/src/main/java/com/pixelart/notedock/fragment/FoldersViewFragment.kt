package com.pixelart.notedock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.CreateFolderDialog
import com.pixelart.notedock.dialog.FolderDialogSuccessListener
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.FABClickedEvent
import com.pixelart.notedock.viewModel.FolderNameTakenEvent
import com.pixelart.notedock.viewModel.FolderViewEvent
import com.pixelart.notedock.viewModel.FoldersViewFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folders_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FoldersViewFragment : Fragment(), FoldersAdapter.OnFolderClickListener {

    private val foldersViewFragmentViewModel: FoldersViewFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folders_view,
            BR.viewmodel to foldersViewFragmentViewModel
        )
        foldersViewFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val foldersAdapter = FoldersAdapter(this)
        setupRecyclerView(foldersAdapter)
        observeLiveData(foldersAdapter)
    }

    private fun setupRecyclerView(foldersAdapter: FoldersAdapter) {
        recyclerViewFolders.layoutManager = LinearLayoutManager(context)
        recyclerViewFolders.adapter = foldersAdapter
    }


    private fun observeLiveData(foldersAdapter: FoldersAdapter) {
        foldersViewFragmentViewModel.loadFolders.observe(this, Observer { folders ->
            foldersAdapter.setNewData(folders)
        })

        foldersViewFragmentViewModel.newFolderCreated.observe(this, Observer { event ->
            when (event) {
                is FolderViewEvent.Success -> { }
                is FolderViewEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        foldersViewFragmentViewModel.fabClicked.observe(this, Observer { event ->
            when (event) {
                FABClickedEvent.Clicked -> createFolderDialog()
            }
        })

        foldersViewFragmentViewModel.isNameTaken.observe(this, Observer { event ->
            when (event) {
                is FolderNameTakenEvent.Success -> {
                    if (event.taken) {
                        //Toast in weird place, maaybe deal with that
                        Toast.makeText(context, "Folder with this name already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        foldersViewFragmentViewModel.uploadFolderModel(FolderModel(event.folderName))
                    }
                }
                is FolderNameTakenEvent.Error -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun createFolderDialog() {
        //Lepsie ale ajtak stale nie velmi barz
        activity?.let {activity ->
            val dialog = CreateFolderDialog(object: FolderDialogSuccessListener {
                override fun onSuccess(folderName: String?) {
                    folderName?.let { foldersViewFragmentViewModel.isNameTaken(it) }
                }
            }).createDialog(activity)
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            dialog.findViewById<EditText>(R.id.editTextFolderName)?.addTextChangedListener { watcher ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !watcher.isNullOrEmpty()
            }
        }
    }

    override fun onFolderClick(uid: String?) {
        uid?.let { uuid ->
            val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToFolderFragment(uuid)
            val navigationRouter = NavigationRouter(view)
            navigationRouter.openAction(action)
        }
    }
}

