package com.pixelart.notedock.dataBinding.adapter

import android.view.MenuItem
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.databinding.BindingAdapter


@BindingAdapter("titleColor")
fun MenuItem.setTitleColor(view: View?, color: Int) {
    val hexColor = Integer.toHexString(color).toUpperCase().substring(2)
    val html = "<font color='#$hexColor'>$title</font>"
    this.title = html.parseAsHtml()
}