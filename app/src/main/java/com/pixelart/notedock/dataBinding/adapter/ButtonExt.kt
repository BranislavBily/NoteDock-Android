package com.pixelart.notedock.dataBinding.adapter

import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter

private const val timeInterval: Long = 350

class ThrottledClickListener(private val listener: View.OnClickListener): View.OnClickListener {
    private var lastTime: Long = 0
    override fun onClick(view: View?) {
        view?.let {
            val currentTime = System.currentTimeMillis()
            if((currentTime - lastTime) > timeInterval) {
                listener.onClick(view)
                lastTime = currentTime
            }
        }
    }
}

@BindingAdapter("onThrottledClick")
fun View.onTrottledClick(listener: View.OnClickListener) {
    setOnClickListener(ThrottledClickListener(listener))
}