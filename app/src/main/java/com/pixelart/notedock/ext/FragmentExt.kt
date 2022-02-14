package com.pixelart.notedock.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.LoginActivity

fun Fragment.hideSoftKeyboard(context: Context, view: View) {
    val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.openSoftKeyBoard(activity: Activity) {
    val view = activity.currentFocus
    val methodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    assert(view != null)
    methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.openLoginActivity() {
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}