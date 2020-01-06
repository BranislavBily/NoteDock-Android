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
import com.pixelart.notedock.viewModel.authentication.ForgotPasswordFragmentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : Fragment() {

    private val forgotPasswordViewModel: ForgotPasswordFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_forgot_password,
            BR.viewmodel to forgotPasswordViewModel
        )
        forgotPasswordViewModel.lifecycleOwner = this
        return dataBinding.root
    }
}
