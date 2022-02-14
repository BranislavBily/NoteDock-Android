package com.pixelart.notedock.ext

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun Int.showAsSnackBar(view: View) {
    Snackbar.make(view, this, Snackbar.LENGTH_LONG).show()
}