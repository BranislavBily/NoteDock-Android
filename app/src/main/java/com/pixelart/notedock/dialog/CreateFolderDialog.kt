package com.pixelart.notedock.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pixelart.notedock.R
import com.pixelart.notedock.domain.usecase.AddFolderImpl
import com.pixelart.notedock.domain.usecase.AddFolderUseCase
import com.pixelart.notedock.model.FolderModel
import kotlinx.android.synthetic.main.create_folder_dialog.view.*
import org.koin.android.ext.android.inject

class CreateFolderDialog : DialogFragment() {

    private val addFolderImpl: AddFolderUseCase by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.create_folder_dialog, null)
            builder.setView(view)
                .setPositiveButton(R.string.create
                ) { dialog, id ->
                    val folderModel = FolderModel()
                    folderModel.name = view.editTextFolderName.text.toString()
                    addFolderImpl.addFolder(folderModel)
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}