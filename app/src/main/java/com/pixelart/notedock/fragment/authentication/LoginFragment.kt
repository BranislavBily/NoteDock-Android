package com.pixelart.notedock.fragment.authentication


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.MainActivity
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ForgotPasswordEvent
import com.pixelart.notedock.viewModel.authentication.LoginEvent
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
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


    private fun observeLiveData() {

        loginFragmentViewModel.loginCompleted.observe(this, Observer { event ->
            view?.let { view ->
                when(event) {
                    is LoginEvent.Success -> {
                        context?.let {
                            val intent = Intent(it, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                    is LoginEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(view)
                    is LoginEvent.BadCredentials -> R.string.invalid_credentials_message.showAsSnackBar(view)
                    is LoginEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is LoginEvent.UnknownError -> R.string.unknown_error_message.showAsSnackBar(view)
                }
            }
        })

        loginFragmentViewModel.forgotPassword.observe(this, SpecificEventObserver<ForgotPasswordEvent> {
            view?.let {view ->
                val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
                val navigationRouter = NavigationRouter(view)
                navigationRouter.openAction(action)
            }
        })

    }
}




