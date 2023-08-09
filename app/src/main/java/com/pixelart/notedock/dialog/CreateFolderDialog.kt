package com.pixelart.notedock.dialog

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pixelart.notedock.R
import com.pixelart.notedock.databinding.CreateFolderDialogBinding

class CreateFolderDialog(
    private val callback: FolderDialogSuccessListener,
) {

    fun createDialog(activity: Activity, inflater: LayoutInflater): AlertDialog {
        val binding = CreateFolderDialogBinding.inflate(inflater)
        val builder = MaterialAlertDialogBuilder(activity).apply {
            setPositiveButton(R.string.create_dialog) { dialog, _ ->
                callback.onSuccess(binding.editTextFolderName.text.toString())
                dialog.cancel()
            }
            setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
                dialog.cancel()
            }
            setView(binding.root)
        }
        return builder.create()
    }
}

interface FolderDialogSuccessListener {
    fun onSuccess(folderName: String?)
}
