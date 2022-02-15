package com.pixelart.notedock.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pixelart.notedock.R

class DeleteFolderDialog(private val callback: FolderDialogDeleteSuccessListener) {

    fun createDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.delete_folder)
            .setPositiveButton(
                R.string.delete_dialog
            ) { dialog, _ ->
                callback.onDelete()
                dialog.cancel()
            }
            .setNegativeButton(
                R.string.cancel_dialog
            ) { dialog, _ ->
                dialog.cancel()
            }
        return builder.create()
    }
}

interface FolderDialogDeleteSuccessListener {
    fun onDelete()
}