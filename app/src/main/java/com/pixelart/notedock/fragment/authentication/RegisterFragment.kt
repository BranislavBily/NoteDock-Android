package com.pixelart.notedock.fragment.authentication


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEventError
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {

        registerFragmentViewModel.alreadyHaveAccount.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            findNavController().popBackStack()
        })

        registerFragmentViewModel.registerButtonPressedEvent.observe(this, SpecificEventObserver<ButtonPressedEvent> {
            val view = view
            val context = context
            if(view != null && context != null) {
                hideKeyboardFrom(context, view)
            }
        })

        registerFragmentViewModel.register.observe(this, Observer { event ->
            view?.let { view ->
                when(event) {
                    is RegisterEvent.Success -> {
                        R.string.account_created.showAsSnackBar(view)
                        findNavController().popBackStack()
                    }
                    is RegisterEvent.Error -> {
                        when(event.error) {
                            is RegisterEventError.EmailAlreadyUsed -> { R.string.email_already_used_message.showAsSnackBar(view) }
                            is RegisterEventError.PasswordsDoNotMatch -> { R.string.passwords_do_not_match_message.showAsSnackBar(view) }
                            is RegisterEventError.NetworkError -> { R.string.network_error_message.showAsSnackBar(view) }
                            is RegisterEventError.InvalidEmail -> { R.string.invalid_email_message.showAsSnackBar(view) }
                            is RegisterEventError.WeakPassword -> { R.string.weak_password_message.showAsSnackBar(view) }
                            is RegisterEventError.UnknownError -> { R.string.unknown_error_message.showAsSnackBar(view) }
                        }
                    }
                }
            }
        })
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
