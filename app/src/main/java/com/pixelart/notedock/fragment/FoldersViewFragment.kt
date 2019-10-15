package com.pixelart.notedock.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.viewModel.FoldersViewViewModel
import kotlinx.android.synthetic.main.fragment_folders_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FoldersViewFragment : Fragment(), FoldersAdapter.OnFolderClickListener {

    private val foldersViewViewModel: FoldersViewViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folders_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewFolders.layoutManager = LinearLayoutManager(context)
        val folderAdapter = FoldersAdapter(this)
        recyclerViewFolders.adapter = folderAdapter
        observeLiveData(folderAdapter)
    }


    private fun observeLiveData(foldersAdapter: FoldersAdapter) {
        foldersViewViewModel.firebaseTest.observe(this, Observer {
            foldersAdapter.setNewData(it)
        })
    }

    override fun onFolderClick(uid: String?) {

        view?.let {view ->
            val navigationController = Navigation.findNavController(view)
            uid?.let { uid ->
                val bundle = bundleOf("uid" to uid)
                navigationController.navigate(R.id.action_foldersViewFragment_to_folderFragment, bundle)
            }
        }


    }


}
