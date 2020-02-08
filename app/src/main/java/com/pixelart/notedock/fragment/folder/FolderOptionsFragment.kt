package com.pixelart.notedock.fragment.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pixelart.notedock.R

class FolderOptionsFragment(private val folderUUID: String,
                            private val onFolderOptionsClickListener: OnFolderOptionsClickListener): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.folder_options_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.textViewEditFolder).setOnClickListener {
            onFolderOptionsClickListener.onClick(folderUUID, Option.EDIT)
            dismiss()
        }
        view.findViewById<TextView>(R.id.textViewDeleteFolder).setOnClickListener {
            onFolderOptionsClickListener.onClick(folderUUID, Option.DELETE)
            dismiss()
        }
    }
}

interface OnFolderOptionsClickListener {
    fun onClick(folderUUID: String, option: Option)
}

enum class Option {
    DELETE,
    EDIT
}