package com.pixelart.notedock.fragment.folder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.SplashActivity
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.CreateFolderDialog
import com.pixelart.notedock.dialog.FolderDialogSuccessListener
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.folder.CreateFolderEvent
import com.pixelart.notedock.viewModel.folder.FolderNameTakenEvent
import com.pixelart.notedock.viewModel.folder.FoldersViewFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folders_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FoldersViewFragment : Fragment(), FoldersAdapter.OnFolderClickListener {

    private val foldersViewFragmentViewModel: FoldersViewFragmentViewModel by viewModel()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folders_view,
            BR.viewmodel to foldersViewFragmentViewModel
        )
        setHasOptionsMenu(true)
        foldersViewFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = FirebaseAuth.getInstance()

        setupToolbar()

        val foldersAdapter = FoldersAdapter(this)
        setupRecyclerView(foldersAdapter)
        observeLiveData(foldersAdapter)
    }

    private fun setupToolbar() {
        toolbarFoldersView?.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.settings -> {
                    val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToSettingsFragment()
                    val navigationRouter = NavigationRouter(view)
                    navigationRouter.openAction(action)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        currentUser?.let {

        } ?: run {
            context?.let { context ->
                val intent = Intent(context, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
            }
        }
        return super.onOptionsItemSelected(item)
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
            view?.let { view ->
                when (event) {
                    is CreateFolderEvent.Success -> { }
                    is CreateFolderEvent.Error -> R.string.error_occurred.showAsSnackBar(view)
                }
            } ?: run {
                Log.e("FolderFragment", "View not found")
            }
        })

        foldersViewFragmentViewModel.fabClicked.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            createFolderDialog()
        })

        foldersViewFragmentViewModel.isNameTaken.observe(this, Observer { event ->
            view?.let { view ->
                when (event) {
                    is FolderNameTakenEvent.Success -> {
                        if (event.taken) {
                            R.string.folder_name_already_exists.showAsSnackBar(view)
                        } else {
                            foldersViewFragmentViewModel.uploadFolderModel(FolderModel(event.folderName))
                        }
                    }
                    is FolderNameTakenEvent.Error -> {
                        R.string.error_occurred.showAsSnackBar(view)
                    }
                }
            } ?: run {
                Log.e("FolderFragment", "View not found")
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

    override fun onFolderClick(uid: String?, name: String?) {
        if(uid != null && name != null) {
                val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToFolderFragment(uid, name)
                val navigationRouter = NavigationRouter(view)
                navigationRouter.openAction(action)
        }
    }
}

