package com.pixelart.notedock.dataBinding.adapter

import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

@BindingAdapter("onBackClicked")
fun Toolbar.onBackClicked(listener: () -> Unit) {
    setNavigationOnClickListener { listener.invoke() }
}