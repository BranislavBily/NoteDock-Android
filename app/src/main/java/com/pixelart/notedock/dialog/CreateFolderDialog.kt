package com.pixelart.notedock.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.pixelart.notedock.R
import kotlinx.android.synthetic.main.create_folder_dialog.view.*

class CreateFolderDialog(
    private val callback: FolderDialogSuccessListener
) {

    fun createDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.create_folder_dialog, null)
        builder.setPositiveButton(R.string.create_dialog) { dialog, _ ->
            callback.onSuccess(view.editTextFolderName.text.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(view)
        return builder.create()
    }
}

interface FolderDialogSuccessListener {
    fun onSuccess(folderName: String?)
}