package com.pixelart.notedock.dialog

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.pixelart.notedock.R
import com.pixelart.notedock.databinding.EditDisplaynameDialogBinding

class EditDisplayNameDialog(
    private val callback: EditDisplaySuccessListener,
) {
    fun createDialog(activity: Activity, inflater: LayoutInflater): AlertDialog {
        val binding = EditDisplaynameDialogBinding.inflate(inflater)
        val builder = AlertDialog.Builder(activity)
        builder.setPositiveButton(R.string.create_dialog) { dialog, _ ->
            callback.onSuccess(binding.editTextDisplayName.text.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(R.string.cancel_dialog) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(binding.root)
        return builder.create()
    }
}

interface EditDisplaySuccessListener {
    fun onSuccess(displayName: String?)
}
