package com.pixelart.notedock.dataBinding.adapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if(visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}