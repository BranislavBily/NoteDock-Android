package com.pixelart.notedock.fragment.authentication


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.MainActivity
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.model.DataEvent
import com.pixelart.notedock.domain.livedata.observer.DataEventObserver
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_login,
            BR.viewmodel to loginFragmentViewModel
        )
        loginFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    override fun onPause() {
        super.onPause()

        editTextPassword.setText("")
    }

    private fun goToMainActivity() {
        context?.let {
            val intent = Intent(it, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun observeLiveData() {

        loginFragmentViewModel.loginCompleted.observe(this, SpecificEventObserver { event ->
            view?.let { view ->
                when(event) {
                    is LoginEvent.Success -> goToMainActivity()
                    is LoginEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(view)
                    is LoginEvent.BadCredentials -> R.string.invalid_credentials_message.showAsSnackBar(view)
                    is LoginEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is LoginEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                    is LoginEvent.UserEmailNotVerified -> showEmailNotVerifiedSnackbar()
                }
            }
        })

        loginFragmentViewModel.forgotPassword.observe(this, SpecificEventObserver {
            view?.let { view ->
                val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
                val navigationRouter = NavigationRouter(view)
                navigationRouter.openAction(action)
            }
        })

        loginFragmentViewModel.createAccount.observe(this, SpecificEventObserver {
            view?.let { view ->
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                val navigationRouter = NavigationRouter(view)
                navigationRouter.openAction(action)
            }
        })

        loginFragmentViewModel.sendEmail.observe(this, SpecificEventObserver { event ->
            view?.let { view ->
                when(event) {
                    is SendEmailEvent.Success -> R.string.verification_email_sent.showAsSnackBar(view)
                    is SendEmailEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })
    }

    private fun showEmailNotVerifiedSnackbar() {
        view?.let { view ->
            Snackbar.make(view, R.string.email_not_verified, Snackbar.LENGTH_SHORT)
                .setAction(R.string.send) {
                    loginFragmentViewModel.sendVerificationEmail()
                }.show()
        }
    }
}




