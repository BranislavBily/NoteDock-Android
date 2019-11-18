package com.pixelart.notedock.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pixelart.notedock.R

class DeleteFolderDialog(private val callback: FolderDialogDeleteSuccessListener): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.delete_folder)
                .setPositiveButton(R.string.delete_dialog
                ) { dialog , _ ->
                    callback.onDelete()
                    dialog.cancel()
                }
                .setNegativeButton(R.string.cancel_dialog
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

interface FolderDialogDeleteSuccessListener {
    fun onDelete()
}