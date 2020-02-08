package com.pixelart.notedock.dialog

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pixelart.notedock.R
import kotlinx.android.synthetic.main.create_folder_dialog.view.*

class EditFolderDialog(
    private val folderName: String,
    private val callback: EditFolderSuccessListener
) {

    fun createDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.edit_folder_dialog, null)
        view.editTextFolderName.setText(folderName, TextView.BufferType.EDITABLE)
        view.editTextFolderName.setSelection(folderName.length)
        builder.setPositiveButton(R.string.create_dialog) { dialog, _ ->
            callback.editFolderClick(view.editTextFolderName.text.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(view)
        return builder.create()
    }
}

interface EditFolderSuccessListener {
    fun editFolderClick(folderName: String)
}