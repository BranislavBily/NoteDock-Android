package com.pixelart.notedock.fragment.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pixelart.notedock.R
import com.pixelart.notedock.fragment.folder.Options

class NoteOptionsFragment(private val noteUUID: String,
                          private val onNoteOptionsClickListener: OnNoteOptionsClickListener): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.note_options_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.textViewNoteDelete).setOnClickListener {
            onNoteOptionsClickListener.onOptionClick(noteUUID, Options.DELETE)
            dismiss()
        }
    }
}

interface OnNoteOptionsClickListener {
    fun onOptionClick(noteUUID: String, options: Options)
}