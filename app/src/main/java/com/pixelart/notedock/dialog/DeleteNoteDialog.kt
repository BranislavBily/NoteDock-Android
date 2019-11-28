package com.pixelart.notedock.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pixelart.notedock.R

class DeleteNoteDialog(private val callback: NoteDialogDeleteSuccessListener): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.delete_note)
                .setMessage(R.string.delete_note_additional)
                .setPositiveButton(
                    R.string.delete_dialog
                ) { dialog , _ ->
                    callback.onDelete()
                    dialog.cancel()
                }
                .setNegativeButton(
                    R.string.cancel_dialog
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

interface NoteDialogDeleteSuccessListener {
    fun onDelete()
}