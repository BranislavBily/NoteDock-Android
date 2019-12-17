package com.pixelart.notedock.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pixelart.notedock.R
import kotlinx.android.synthetic.main.create_folder_dialog.view.*

class CreateFolderDialog(
    private val callback: FolderDialogSuccessListener
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.create_folder_dialog, null)
            builder.setView(view)
                .setPositiveButton(R.string.create_dialog)
                 { dialog, _ ->
                    callback.onSuccess(view.editTextFolderName.text.toString())
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
interface FolderDialogSuccessListener {
    fun onSuccess(folderName: String?)
}