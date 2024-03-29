package com.pixelart.notedock.fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.MainActivity
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.databinding.FragmentLoginBinding
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.LoginEvent
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
import com.pixelart.notedock.viewModel.authentication.SendEmailEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModel()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = setupDataBinding(
            R.layout.fragment_login,
            BR.viewmodel to loginFragmentViewModel,
        )
        loginFragmentViewModel.lifecycleOwner = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    private fun goToMainActivity() {
        context?.let {
            val intent = Intent(it, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun observeLiveData() {
        loginFragmentViewModel.loginCompleted.observe(
            viewLifecycleOwner,
            SpecificEventObserver { event ->
                view?.let { view ->
                    when (event) {
                        is LoginEvent.Success -> goToMainActivity()
                        is LoginEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(
                            view,
                        )

                        is LoginEvent.BadCredentials -> R.string.invalid_credentials_message.showAsSnackBar(
                            view,
                        )

                        is LoginEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(
                            view,
                        )

                        is LoginEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                        is LoginEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(
                            view,
                        )

                        is LoginEvent.UserEmailNotVerified -> showEmailNotVerifiedSnackBar()
                    }
                }
            },
        )

        loginFragmentViewModel.forgotPassword.observe(
            viewLifecycleOwner,
            SpecificEventObserver {
                NavigationRouter(view).loginToForgotPassword()
            },
        )

        loginFragmentViewModel.createAccount.observe(
            viewLifecycleOwner,
            SpecificEventObserver {
                NavigationRouter(view).loginToRegister()
            },
        )

        loginFragmentViewModel.sendEmail.observe(
            viewLifecycleOwner,
            SpecificEventObserver { event ->
                view?.let { view ->
                    when (event) {
                        is SendEmailEvent.Success -> R.string.verification_email_sent.showAsSnackBar(
                            view,
                        )

                        is SendEmailEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(
                            view,
                        )

                        is SendEmailEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(
                            view,
                        )

                        is SendEmailEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(
                            view,
                        )
                    }
                }
            },
        )
    }

    private fun showEmailNotVerifiedSnackBar() {
        view?.let { view ->
            Snackbar.make(view, R.string.email_not_verified, Snackbar.LENGTH_LONG)
                .setAction(R.string.send) {
                    loginFragmentViewModel.sendVerificationEmail()
                }.show()
        }
    }
}
