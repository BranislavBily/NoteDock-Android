package com.pixelart.notedock.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri

fun Activity.openMailApp(email: String, subject: String? = null, text: String? = null) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
    }
    subject?.let { intent.putExtra(Intent.EXTRA_SUBJECT, it) }
    text?.let { intent.putExtra(Intent.EXTRA_TEXT, it) }
    startActivity(intent)
}