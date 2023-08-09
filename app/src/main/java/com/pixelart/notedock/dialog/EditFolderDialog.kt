package com.pixelart.notedock.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pixelart.notedock.R
import com.pixelart.notedock.databinding.EditFolderDialogBinding

class EditFolderDialog(
    private val folderName: String,
    private val callback: EditFolderSuccessListener,
) {

    fun createDialog(activity: Activity, inflater: LayoutInflater): AlertDialog {
        val binding = EditFolderDialogBinding.inflate(inflater)
        val builder = AlertDialog.Builder(activity)
        binding.editTextFolderName.setText(folderName, TextView.BufferType.EDITABLE)
        binding.editTextFolderName.setSelection(folderName.length)
        builder.setPositiveButton(R.string.action_rename) { dialog, _ ->
            callback.editFolderClick(binding.editTextFolderName.text.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(binding.root)
        return builder.create()
    }
}

interface EditFolderSuccessListener {
    fun editFolderClick(folderName: String)
}
