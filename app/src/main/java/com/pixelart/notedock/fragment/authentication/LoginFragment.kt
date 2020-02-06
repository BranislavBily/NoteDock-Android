package com.pixelart.notedock.fragment.authentication


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.pixelart.notedock.viewModel.EyeViewModel
import com.pixelart.notedock.viewModel.authentication.*
import io.opencensus.stats.ViewData
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.log

class LoginFragment : Fragment() {

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModel()
    private val eyeViewModel: EyeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_login,
            BR.viewmodel to loginFragmentViewModel, BR.eyeviewmodel to eyeViewModel
        )
        loginFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        Log.i("Color", Color.BLACK.toString())
        Log.i("Color", Integer.toHexString(Color.BLACK).toUpperCase().substring(2))
        editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            eyeViewModel.changeEyeVisibility(hasFocus)
        }
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

        loginFragmentViewModel.loginCompleted.observe(viewLifecycleOwner, SpecificEventObserver { event ->
            view?.let { view ->
                when(event) {
                    is LoginEvent.Success -> goToMainActivity()
                    is LoginEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(view)
                    is LoginEvent.BadCredentials -> R.string.invalid_credentials_message.showAsSnackBar(view)
                    is LoginEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is LoginEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                    is LoginEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(view)
                    is LoginEvent.UserEmailNotVerified -> showEmailNotVerifiedSnackbar()
                }
            }
        })

        eyeViewModel.eyeOpen.observe(viewLifecycleOwner, Observer { eyeOpen ->
            if (eyeOpen) {
                val selection = editTextPassword.selectionStart
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextPassword.setSelection(selection)
            } else {
                val selection = editTextPassword.selectionStart
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextPassword.setSelection(selection)
            }
        })

        loginFragmentViewModel.forgotPassword.observe(viewLifecycleOwner, SpecificEventObserver {
            NavigationRouter(view).loginToForgotPassword()
        })

        loginFragmentViewModel.createAccount.observe(viewLifecycleOwner, SpecificEventObserver {
            NavigationRouter(view).loginToRegister()
        })

        loginFragmentViewModel.sendEmail.observe(viewLifecycleOwner, SpecificEventObserver { event ->
            view?.let { view ->
                when(event) {
                    is SendEmailEvent.Success -> R.string.verification_email_sent.showAsSnackBar(view)
                    is SendEmailEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                    is SendEmailEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(view)
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




