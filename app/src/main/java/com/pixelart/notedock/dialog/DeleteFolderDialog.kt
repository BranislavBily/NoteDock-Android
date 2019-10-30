package com.pixelart.notedock.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.R
import com.pixelart.notedock.domain.usecase.DeleteFolderUseCase
import com.pixelart.notedock.model.FolderModel
import org.koin.android.ext.android.inject

class DeleteFolderDialog(private val folderModel: FolderModel): DialogFragment() {

    private val deleteFolderImpl: DeleteFolderUseCase by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.delete_folder)
                .setPositiveButton(R.string.delete_dialog
                ) { dialog, id ->
                    //This needs to be processed in viewModel
                    deleteFolderImpl.deleteFolder(folderModel, EventListener { exception, _ ->
                        exception?.let {
                            dialog.cancel()
                        }
                    })
                }
                .setNegativeButton(R.string.cancel_dialog
                ) { dialog, id ->
                    dialog.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}