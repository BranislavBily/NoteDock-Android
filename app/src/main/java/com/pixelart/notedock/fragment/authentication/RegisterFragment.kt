package com.pixelart.notedock.fragment.authentication

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
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEventError
import com.pixelart.notedock.viewModel.authentication.RegisterFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val registerFragmentViewModel: RegisterFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_register,
            BR.viewmodel to registerFragmentViewModel,
        )
        registerFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {
        registerFragmentViewModel.alreadyHaveAccount.observe(
            viewLifecycleOwner,
            SpecificEventObserver<ButtonPressedEvent> {
                findNavController().popBackStack()
            },
        )

        registerFragmentViewModel.registerButtonPressedEvent.observe(
            viewLifecycleOwner,
            SpecificEventObserver<ButtonPressedEvent> {
                val view = view
                val context = context
                if (view != null && context != null) {
                    hideSoftKeyboard(context, view)
                }
            },
        )

        registerFragmentViewModel.register.observe(
            viewLifecycleOwner,
            Observer { event ->
                view?.let { view ->
                    when (event) {
                        is RegisterEvent.Success -> {
                            R.string.account_created.showAsSnackBar(view)
                            findNavController().popBackStack()
                        }

                        is RegisterEvent.Error -> {
                            when (event.error) {
                                is RegisterEventError.EmailAlreadyUsed -> {
                                    R.string.email_already_used_message.showAsSnackBar(view)
                                }

                                is RegisterEventError.PasswordsDoNotMatch -> {
                                    R.string.passwords_do_not_match_message.showAsSnackBar(view)
                                }

                                is RegisterEventError.NetworkError -> {
                                    R.string.network_error_message.showAsSnackBar(view)
                                }

                                is RegisterEventError.InvalidEmail -> {
                                    R.string.invalid_email_message.showAsSnackBar(view)
                                }

                                is RegisterEventError.WeakPassword -> {
                                    R.string.weak_password_message.showAsSnackBar(view)
                                }

                                is RegisterEventError.TooManyRequests -> {
                                    R.string.too_many_requests.showAsSnackBar(view)
                                }

                                is RegisterEventError.UnknownError -> {
                                    R.string.error_occurred.showAsSnackBar(view)
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}
