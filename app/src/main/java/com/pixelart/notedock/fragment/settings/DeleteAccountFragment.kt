package com.pixelart.notedock.fragment.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.pixelart.notedock.BR

import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.viewModel.settings.ChangeEmailViewModel
import com.pixelart.notedock.viewModel.settings.DeleteAccountViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class DeleteAccountFragment : Fragment() {

    private val deleteAccountViewModel: DeleteAccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_delete_account,
            BR.viewmodel to deleteAccountViewModel
        )
        setHasOptionsMenu(true)
        deleteAccountViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        observeLiveData()
    }

    private fun observeLiveData() {
        deleteAccountViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
    }
}