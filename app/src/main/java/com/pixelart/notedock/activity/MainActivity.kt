package com.pixelart.notedock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.setupDataBinding
import com.pixelart.notedock.viewModel.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDataBinding<ViewDataBinding>(R.layout.activity_main, BR.viewModel to mainViewModel)
    }
}
