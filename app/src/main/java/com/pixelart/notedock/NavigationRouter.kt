package com.pixelart.notedock

import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation

class NavigationRouter(private val view: View?) {
    fun openAction(action: NavDirections) {
        view?.let {
            val navigationController = Navigation.findNavController(view)
            navigationController.navigate(action)
        }
    }
}