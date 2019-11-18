package com.pixelart.notedock

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation

class NavigationRouter(private val view: View?) {
    fun openFragment(actionID: Int, uid: String) {
        view?.let {
            val navigationController = Navigation.findNavController(view)
            val bundle = bundleOf("uid" to uid)
            navigationController.navigate(actionID, bundle)
        }
    }

    fun openFragment(actionID: Int) {
        view?.let {
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(
                R.id.action_foldersViewFragment_to_folderFragment
            )
        }
    }
}