package com.pixelart.notedock.fragment.authentication


import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.pixelart.notedock.viewModel.EyeViewModel
import com.pixelart.notedock.viewModel.EyeViewModel2
import com.pixelart.notedock.viewModel.authentication.ButtonPressedEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEvent
import com.pixelart.notedock.viewModel.authentication.RegisterEventError
import com.pixelart.notedock.viewModel.authentication.RegisterFragmentViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.viewmodel.ext.android.viewModel


class RegisterFragment : Fragment() {

    private val registerFragmentViewModel: RegisterFragmentViewModel by viewModel()
    private val eyeViewModel: EyeViewModel by viewModel()
    private val eyeViewModelConfirm: EyeViewModel2 by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_register,
            BR.viewmodel to registerFragmentViewModel,
                    BR.eyeviewmodel to eyeViewModel,
            BR.eyeviewmodelconfirm to eyeViewModelConfirm
        )
        registerFragmentViewModel.lifecycleOwner = this
        eyeViewModel.lifecycleOwner = this
        eyeViewModelConfirm.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            eyeViewModel.changeEyeVisibility(hasFocus)
        }

        editTextConfirmPassword.setOnFocusChangeListener { v, hasFocus ->
            eyeViewModelConfirm.changeEyeVisibility(hasFocus)
        }

        observeLiveData()
    }

    private fun observeLiveData() {

        registerFragmentViewModel.alreadyHaveAccount.observe(viewLifecycleOwner, SpecificEventObserver<ButtonPressedEvent> {
            findNavController().popBackStack()
        })

        registerFragmentViewModel.registerButtonPressedEvent.observe(viewLifecycleOwner, SpecificEventObserver<ButtonPressedEvent> {
            val view = view
            val context = context
            if(view != null && context != null) {
                hideSoftKeyboard(context, view)
            }
        })

        registerFragmentViewModel.register.observe(viewLifecycleOwner, Observer { event ->
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
                            is RegisterEventError.TooManyRequests -> { R.string.too_many_requests.showAsSnackBar(view) }
                            is RegisterEventError.UnknownError -> { R.string.error_occurred.showAsSnackBar(view) }
                        }
                    }
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

        eyeViewModelConfirm.eyeOpen.observe(viewLifecycleOwner, Observer { eyeOpen ->
            if (eyeOpen) {
                val selection = editTextConfirmPassword.selectionStart
                editTextConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextConfirmPassword.setSelection(selection)
            } else {
                val selection = editTextConfirmPassword.selectionStart
                editTextConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextConfirmPassword.setSelection(selection)
            }
        })
    }
}
