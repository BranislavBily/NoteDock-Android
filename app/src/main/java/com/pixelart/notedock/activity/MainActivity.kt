package com.pixelart.notedock.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.pixelart.notedock.R
import com.pixelart.notedock.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeLiveData()
    }

    private fun observeLiveData() {
        mainViewModel.userNameLd.observe(this, Observer {
            textField.text = it
        })
    }
}
