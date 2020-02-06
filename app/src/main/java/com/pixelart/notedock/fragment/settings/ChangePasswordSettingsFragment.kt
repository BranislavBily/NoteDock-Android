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
import com.pixelart.notedock.viewModel.settings.ChangePasswordViewModel
import kotlinx.android.synthetic.main.fragment_change_password_settings.*
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

        setupToolbar()
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
    }
}
