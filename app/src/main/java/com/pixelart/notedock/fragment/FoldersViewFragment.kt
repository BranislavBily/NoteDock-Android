package com.pixelart.notedock.fragment


import android.icu.text.CaseMap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.dialog.CreateFolderDialog
import com.pixelart.notedock.dialog.FolderDialogCreateListener
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.setupDataBinding
import com.pixelart.notedock.viewModel.FABClickedEvent
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


        recyclerViewFolders.layoutManager = LinearLayoutManager(context)
        val folderAdapter = FoldersAdapter(this)
        recyclerViewFolders.adapter = folderAdapter
        observeLiveData(folderAdapter)
    }


    private fun observeLiveData(foldersAdapter: FoldersAdapter) {
        foldersViewFragmentViewModel.firebaseTest.observe(this, Observer {
            foldersAdapter.setNewData(it)
        })

        foldersViewFragmentViewModel.fabClicked.observe(this, Observer {
            when(it) {
                FABClickedEvent.Clicked -> createDialog()
            }
        })

        foldersViewFragmentViewModel.newFolderCreated.observe(this, Observer {
            when(it) {
                FolderViewEvent.Success -> Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                FolderViewEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onFolderClick(uid: String?) {

        view?.let { view ->
            val navigationController = Navigation.findNavController(view)
            uid?.let { uid ->
                val bundle = bundleOf("uid" to uid)
                navigationController.navigate(
                    R.id.action_foldersViewFragment_to_folderFragment,
                    bundle
                )
            }
        }
    }

    private fun createDialog() {
        val dialog = CreateFolderDialog(object : FolderDialogCreateListener {
            override fun onCreate(name: String) {
                val folderModel = FolderModel().apply { this.name = name }
                foldersViewFragmentViewModel.uploadFolderModel(folderModel)
            }
        })
        fragmentManager?.let {
            dialog.show(it, "CreateFolderDialog")
        }
    }
}
