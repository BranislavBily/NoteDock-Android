package com.pixelart.notedock.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.settings.ChangePasswordEvent
import com.pixelart.notedock.viewModel.settings.ChangePasswordViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ChangePasswordSettingsFragment : Fragment() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_change_password_settings,
            BR.viewmodel to changePasswordViewModel
        )
        setHasOptionsMenu(true)
        changePasswordViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        observeLiveData()
    }

    private fun observeLiveData() {
        changePasswordViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        changePasswordViewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            if(loading) {
                context?.let {context ->
                    view?.let { view ->
                        hideSoftKeyboard(context, view)
                    }
                }
            }
        })

        changePasswordViewModel.changePassword.observe(viewLifecycleOwner, SpecificEventObserver { event ->
            view?.let { view ->
                context?.let {context ->
                        hideSoftKeyboard(context, view)
                }
                when(event) {
                    is ChangePasswordEvent.Success -> {
                        R.string.password_changed.showAsSnackBar(view)
                        findNavController().popBackStack()
                    }
                    is ChangePasswordEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is ChangePasswordEvent.FillAllFields -> R.string.please_fill_all_fields.showAsSnackBar(view)
                    is ChangePasswordEvent.NewPasswordCannotBeCurrent -> R.string.new_password_cannot_be_current.showAsSnackBar(view)
                    is ChangePasswordEvent.PasswordsDoNotMatch -> R.string.passwords_do_not_match_message.showAsSnackBar(view)
                    is ChangePasswordEvent.WeakPassword -> R.string.weak_password_message.showAsSnackBar(view)
                    is ChangePasswordEvent.UserNotFound -> R.string.no_user_found.showAsSnackBar(view)
                    is ChangePasswordEvent.WrongPassword -> R.string.wrong_password.showAsSnackBar(view)
                    is ChangePasswordEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(view)
                    is ChangePasswordEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })
    }
}
