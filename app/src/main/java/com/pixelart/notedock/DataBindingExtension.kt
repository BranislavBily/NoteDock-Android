package com.pixelart.notedock

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

fun <B : ViewDataBinding> AppCompatActivity.setupDataBinding(layoutId: Int, vararg variables: Pair<Int, Any>): B {
    val dataBinding: B = DataBindingUtil.setContentView(
        this,
        layoutId)
    dataBinding.lifecycleOwner = this
    variables.forEach {
        dataBinding.setVariable(it.first, it.second)
    }
    return dataBinding
}

fun <B : ViewDataBinding> Fragment.setupDataBinding(layoutId: Int, vararg variables: Pair<Int, Any>): B {
    val dataBinding: B = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false)
    dataBinding.lifecycleOwner = this
    variables.forEach {
        dataBinding.setVariable(it.first, it.second)
    }
    return dataBinding
}