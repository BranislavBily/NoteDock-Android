package com.pixelart.notedock.fragment.authentication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pixelart.notedock.BR

import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.authentication.RecoverAccountEvent
import com.pixelart.notedock.viewModel.authentication.RecoverAccountEventError
import com.pixelart.notedock.viewModel.authentication.ResetPasswordFragmentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ResetPasswordFragment : Fragment() {

    private val resetPasswordViewModel: ResetPasswordFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_reset_password,
            BR.viewmodel to resetPasswordViewModel
        )
        resetPasswordViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {
        resetPasswordViewModel.backToLogin.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            findNavController().popBackStack()
        })

        resetPasswordViewModel.recoverAccountPressed.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            val view = view
            val context = context
            if(view != null && context != null) {
                hideSoftKeyboard(context, view)
            }
        })

        resetPasswordViewModel.recoverAccount.observe(this, Observer { event ->
            view?.let { view ->
                when(event) {
                    is RecoverAccountEvent.Success -> {
                        R.string.recover_email_sent.showAsSnackBar(view)
                        findNavController().popBackStack()
                    }
                    is RecoverAccountEvent.Error -> {
                        when(event.error) {
                            is RecoverAccountEventError.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(view)
                            is RecoverAccountEventError.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                            is RecoverAccountEventError.UnknownError -> R.string.unknown_error_message.showAsSnackBar(view)
                        }
                    }
                }
            }
        })
    }


}