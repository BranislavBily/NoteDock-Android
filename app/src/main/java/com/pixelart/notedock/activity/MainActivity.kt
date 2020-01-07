package com.pixelart.notedock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.pixelart.notedock.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = mainToolbar
        toolbar.title = "Folders"
        setSupportActionBar(toolbar)
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.fragment).navigateUp()
}
