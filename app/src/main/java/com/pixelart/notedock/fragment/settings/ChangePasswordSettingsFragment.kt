package com.pixelart.notedock.fragment.settings

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
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.EyeViewModel
import com.pixelart.notedock.viewModel.settings.ChangePasswordEvent
import com.pixelart.notedock.viewModel.settings.ChangePasswordViewModel
import kotlinx.android.synthetic.main.fragment_change_password_settings.*
import org.koin.android.viewmodel.ext.android.viewModel

class ChangePasswordSettingsFragment : Fragment() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModel()
    private val eyeViewModel: EyeViewModel by viewModel()
    private val eyeViewModel2: EyeViewModel by viewModel()
    private val eyeViewModel3: EyeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_change_password_settings,
            BR.viewmodel to changePasswordViewModel,
            BR.eyeviewmodel to eyeViewModel,
            BR.eyeviewmodel2 to eyeViewModel2,
            BR.eyeviewmodel3 to eyeViewModel3
        )
        setHasOptionsMenu(true)
        changePasswordViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        setupToolbar()
        editTextCurrentPassword.setOnFocusChangeListener { _, hasFocus ->
            eyeViewModel.changeEyeVisibility(hasFocus)
        }
        editTextNewPassword.setOnFocusChangeListener { _, hasFocus ->
            eyeViewModel2.changeEyeVisibility(hasFocus)
        }
        editTextConfirmNewPassword.setOnFocusChangeListener { _, hasFocus ->
            eyeViewModel3.changeEyeVisibility(hasFocus)
        }
        observeLiveData()
    }

    private fun setupToolbar() {
        toolbarChangePassword?.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId) {
                R.id.menu_item_save -> {
                    changePasswordViewModel.saveNewPassword()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeLiveData() {
        changePasswordViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        changePasswordViewModel.savePasswordEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            toolbarChangePassword?.menu?.getItem(0)?.isEnabled = enabled
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
                when(event) {
                    is ChangePasswordEvent.Success -> {
                        R.string.password_changed.showAsSnackBar(view)
                        findNavController().popBackStack()
                    }
                    is ChangePasswordEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is ChangePasswordEvent.FillAllFields -> R.string.please_fill_all_fields.showAsSnackBar(view)
                    is ChangePasswordEvent.NewPasswordCannotBeOld -> R.string.new_password_cannot_be_old.showAsSnackBar(view)
                    is ChangePasswordEvent.PasswordsDoNotMatch -> R.string.passwords_do_not_match_message.showAsSnackBar(view)
                    is ChangePasswordEvent.WeakPassword -> R.string.weak_password_message.showAsSnackBar(view)
                    is ChangePasswordEvent.UserNotFound -> R.string.no_user_found.showAsSnackBar(view)
                    is ChangePasswordEvent.WrongPassword -> R.string.wrong_password.showAsSnackBar(view)
                    is ChangePasswordEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(view)
                    is ChangePasswordEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })

        eyeViewModel.eyeOpen.observe(viewLifecycleOwner, Observer { eyeOpen ->
            if (eyeOpen) {
                val selection = editTextCurrentPassword.selectionStart
                editTextCurrentPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextCurrentPassword.setSelection(selection)
            } else {
                val selection = editTextCurrentPassword.selectionStart
                editTextCurrentPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextCurrentPassword.setSelection(selection)
            }
        })

        eyeViewModel2.eyeOpen.observe(viewLifecycleOwner, Observer { eyeOpen ->
            if (eyeOpen) {
                val selection = editTextNewPassword.selectionStart
                editTextNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextNewPassword.setSelection(selection)
            } else {
                val selection = editTextNewPassword.selectionStart
                editTextNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextNewPassword.setSelection(selection)
            }
        })

        eyeViewModel3.eyeOpen.observe(viewLifecycleOwner, Observer { eyeOpen ->
            if (eyeOpen) {
                val selection = editTextConfirmNewPassword.selectionStart
                editTextConfirmNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextConfirmNewPassword.setSelection(selection)
            } else {
                val selection = editTextConfirmNewPassword.selectionStart
                editTextConfirmNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextConfirmNewPassword.setSelection(selection)
            }
        })
    }
}
