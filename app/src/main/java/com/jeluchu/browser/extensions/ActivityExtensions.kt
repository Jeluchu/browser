package com.jeluchu.browser.extensions

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.InputMethodManager

fun Activity.share(url: String?) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
        putExtra(Intent.EXTRA_SUBJECT, "URL")
    }
    startActivity(Intent.createChooser(shareIntent, "Share with your friends"))
}

fun Activity.hideKeyboard() {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        currentFocus?.windowToken,
        InputMethodManager.SHOW_FORCED
    )
}