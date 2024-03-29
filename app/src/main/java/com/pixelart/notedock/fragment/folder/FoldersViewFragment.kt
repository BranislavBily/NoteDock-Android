package com.pixelart.notedock.fragment.folder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.databinding.FragmentFoldersViewBinding
import com.pixelart.notedock.dialog.*
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.folder.CreateFolderEvent
import com.pixelart.notedock.viewModel.folder.FoldersViewFragmentViewModel
import com.pixelart.notedock.viewModel.folder.LoadFoldersEvent
import com.pixelart.notedock.viewModel.folder.RenameFolderEvent
import com.pixelart.notedock.viewModel.note.GenericCRUDEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class FoldersViewFragment :
    Fragment(),
    FoldersAdapter.OnFolderClickListener,
    OnFolderOptionsClickListener {

    private val foldersViewFragmentViewModel: FoldersViewFragmentViewModel by viewModel()

    private lateinit var dataBinding: FragmentFoldersViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataBinding = setupDataBinding(
            R.layout.fragment_folders_view,
            BR.viewmodel to foldersViewFragmentViewModel,
        )
        setHasOptionsMenu(true)
        foldersViewFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        val foldersAdapter = FoldersAdapter(this)
        setupRecyclerView(foldersAdapter)
        observeLiveData(foldersAdapter)
    }

    override fun onResume() {
        super.onResume()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            user.reload()
                .addOnFailureListener { error ->
                    if (error !is FirebaseNetworkException) {
                        openLoginActivity()
                    }
                }
        }
    }

    private fun setupToolbar() {
        dataBinding.toolbarFoldersView.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    NavigationRouter(view).foldersToSettings()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun setupRecyclerView(foldersAdapter: FoldersAdapter) {
        dataBinding.recyclerViewFolders.layoutManager = LinearLayoutManager(context)
        dataBinding.recyclerViewFolders.adapter = foldersAdapter
    }

    private fun observeLiveData(foldersAdapter: FoldersAdapter) {
        foldersViewFragmentViewModel.loadFolders.observe(
            viewLifecycleOwner,
            Observer { event ->
                view?.let { view ->
                    when (event) {
                        is LoadFoldersEvent.Success -> foldersAdapter.setNewData(event.folders)
                        is LoadFoldersEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                        is LoadFoldersEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(
                            view,
                        )
                    }
                }
            },
        )

        foldersViewFragmentViewModel.newFolderCreated.observe(
            viewLifecycleOwner,
            Observer { event ->
                view?.let { view ->
                    when (event) {
                        is CreateFolderEvent.Success -> {
                        }

                        is CreateFolderEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                        is CreateFolderEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(
                            view,
                        )

                        is CreateFolderEvent.FolderNameTaken -> R.string.folder_name_already_exists.showAsSnackBar(
                            view,
                        )
                    }
                } ?: run {
                    Log.e("FolderFragment", "View not found")
                }
            },
        )

        foldersViewFragmentViewModel.deleteFolder.observe(
            viewLifecycleOwner,
            Observer { event ->
                view?.let { view ->
                    when (event) {
                        is GenericCRUDEvent.Success -> {
                        }

                        is GenericCRUDEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                        is GenericCRUDEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(
                            view,
                        )
                    }
                }
            },
        )

        foldersViewFragmentViewModel.fabClicked.observe(
            viewLifecycleOwner,
            SpecificEventObserver<ButtonPressedEvent> {
                createFolderDialog()
            },
        )

        foldersViewFragmentViewModel.renameFolder.observe(
            viewLifecycleOwner,
            Observer { event ->
                view?.let { view ->
                    when (event) {
                        is RenameFolderEvent.Success -> {
                        }

                        is RenameFolderEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                        is RenameFolderEvent.FolderNameTaken -> R.string.folder_name_already_exists.showAsSnackBar(
                            view,
                        )

                        is RenameFolderEvent.NoUserFound -> R.string.no_user_found.showAsSnackBar(
                            view,
                        )
                    }
                }
            },
        )
    }

    private fun createFolderDialog() {
        activity?.let { activity ->
            CreateFolderDialog(object : FolderDialogSuccessListener {
                override fun onSuccess(folderName: String?) {
                    folderName?.let { foldersViewFragmentViewModel.isNameTaken(it) }
                }
            })
                .createDialog(activity, layoutInflater)
                .apply {
                    show()
                    getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    findViewById<EditText>(R.id.editTextFolderName)
                        ?.addTextChangedListener { watcher ->
                            getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                                !watcher.isNullOrEmpty()
                        }
                }
        }
    }

    override fun onFolderClick(uid: String?, name: String?) {
        if (uid != null && name != null) {
            NavigationRouter(view).foldersToFolder(uid, name)
        }
    }

    override fun onFolderLongPress(uid: String?, folderName: String?) {
        uid?.let { uuid ->
            folderName?.let { folderName ->
                val optionsFragment = FolderOptionsFragment(uuid, folderName, this)
                optionsFragment.show(parentFragmentManager, "FolderOptionsFragment")
            }
        }
    }

    override fun onClick(folderUUID: String, folderName: String, options: Options) {
        if (options == Options.DELETE) {
            activity?.let { activity ->
                val dialog = DeleteFolderDialog(object : FolderDialogDeleteSuccessListener {
                    override fun onDelete() {
                        foldersViewFragmentViewModel.deleteFolder(folderUUID)
                    }
                }).createDialog(activity)
                dialog.show()
            }
        } else {
            activity?.let { activity ->
                val dialog = EditFolderDialog(
                    folderName,
                    object : EditFolderSuccessListener {
                        override fun editFolderClick(folderName: String) {
                            foldersViewFragmentViewModel.renameFolder(folderUUID, folderName)
                        }
                    },
                ).createDialog(activity, layoutInflater)
                dialog.show()
                dialog.findViewById<EditText>(R.id.editTextFolderName)
                    ?.addTextChangedListener { watcher ->
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                            !watcher.isNullOrEmpty()
                    }
            }
        }
    }
}
