package com.pixelart.notedock

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.pixelart.notedock.fragment.authentication.LoginFragmentDirections
import com.pixelart.notedock.fragment.folder.FolderFragmentDirections
import com.pixelart.notedock.fragment.folder.FoldersViewFragmentDirections
import com.pixelart.notedock.fragment.settings.SettingsFragmentDirections

class NavigationRouter(private val view: View?) {

    fun settingsToAccount() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToAccountSettingsFragment()
        openAction(action)
    }

    fun settingsToChangePassword() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToChangePasswordSettingsFragment()
        openAction(action)
    }

    fun settingsToHelpAndSupport() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToHelpAndSupportSettingsFragment()
        openAction(action)
    }

    fun folderToNote(folderUUID: String, noteUUID: String) {
        val action = FolderFragmentDirections.actionFolderFragmentToNoteFragment(folderUUID, noteUUID)
        openAction(action)
    }

    fun foldersToSettings() {
        val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToSettingsFragment()
        openAction(action)
    }

    fun foldersToFolder(folderUUID: String, folderName: String) {
        val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToFolderFragment(folderUUID, folderName)
        openAction(action)
    }

    fun loginToRegister() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        openAction(action)
    }

    fun loginToForgotPassword() {
        val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
        openAction(action)
    }

    private fun openAction(action: NavDirections) {
        view?.let {
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }
}