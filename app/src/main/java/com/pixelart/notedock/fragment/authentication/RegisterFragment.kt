package com.pixelart.notedock.fragment.authentication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.pixelart.notedock.BR

import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.RegisterFragmentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val registerFragmentViewModel: RegisterFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_register,
            BR.viewmodel to registerFragmentViewModel
        )
        registerFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }
}
