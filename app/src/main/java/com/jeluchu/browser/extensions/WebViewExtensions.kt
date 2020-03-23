package com.jeluchu.browser.extensions

import android.app.Activity
import android.util.Patterns
import android.webkit.WebView
import android.widget.EditText
import java.util.*

fun WebView.searchOrLoad(activity: Activity, text: String) {
    if (Patterns.WEB_URL.matcher(text.toLowerCase(Locale.ROOT)).matches()) {
        if (text.contains("http://") || text.contains("https://")) { loadUrl(text) }
        else { loadUrl("http://$text") }
    } else { loadUrl("https://www.google.com/search?q=$text") }

    activity.hideKeyboard()

}


fun WebView.go(activity: Activity, editText: EditText) = searchOrLoad(activity, editText.text.toString())
fun WebView.goBack() { if (canGoBack()) goBack() }
fun WebView.goForward() { if (canGoForward()) goForward() }
fun WebView.goHome() = loadUrl("file:///android_asset/demo.html")
fun WebView.refresh() = reload()