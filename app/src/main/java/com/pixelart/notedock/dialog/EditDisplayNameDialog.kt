package com.pixelart.notedock.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.pixelart.notedock.R
import kotlinx.android.synthetic.main.create_folder_dialog.view.*
import kotlinx.android.synthetic.main.edit_displayname_dialog.view.*

class EditDisplayNameDialog(
    private val callback: EditDisplaySuccessListener
) {

    fun createDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.edit_displayname_dialog, null)
        builder.setPositiveButton(R.string.save) { dialog, _ ->
            callback.onSuccess(view.editTextDisplayName.text.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(view)
        return builder.create()
    }
}

interface EditDisplaySuccessListener {
    fun onSuccess(displayName: String?)
}