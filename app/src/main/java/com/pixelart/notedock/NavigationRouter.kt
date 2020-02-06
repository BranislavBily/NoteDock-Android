package com.pixelart.notedock

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.pixelart.notedock.fragment.settings.SettingsFragmentDirections

class NavigationRouter(private val view: View?) {

    fun settingsToAccount() {
        view?.let { view ->
            val action = SettingsFragmentDirections.actionSettingsFragmentToAccountSettingsFragment()
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }

    fun settingsToChangePassword() {
        view?.let { view ->
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangePasswordSettingsFragment()
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }

    fun settingsToHelpAndSupport() {
        view?.let { view ->
            val action = SettingsFragmentDirections.actionSettingsFragmentToHelpAndSupportSettingsFragment()
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }

    fun openAction(action: NavDirections) {
        view?.let {
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }
}