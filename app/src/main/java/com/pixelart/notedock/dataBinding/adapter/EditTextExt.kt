package com.pixelart.notedock.dataBinding.adapter

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("onDone")
fun EditText.onActionDone(listener: View.OnClickListener) {
    setOnEditorActionListener { textView, i, _ ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            listener.onClick(textView)
        }
        false
    }
}