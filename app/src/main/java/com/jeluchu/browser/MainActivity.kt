package com.jeluchu.browser

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jeluchu.browser.extensions.go
import com.jeluchu.browser.extensions.goHome
import com.jeluchu.browser.extensions.refresh
import com.jeluchu.browser.extensions.share
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var urlShare: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()

        webview.loadUrl("file:///android_asset/demo.html")
        setSupportActionBar(toolbar)
        webview.settings.javaScriptEnabled = true

        webview.setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadDialog(url, userAgent, contentDisposition, mimetype)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            } else {
                downloadDialog(url, userAgent, contentDisposition, mimetype)
            }
        }

        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressbar.progress = newProgress
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) { progressbar.visibility = View.GONE }
            }
        }

        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressbar.visibility = View.VISIBLE
                if ("file:///android_asset/demo.html" != url) { etSearchQuery.setText(url) }
                else { etSearchQuery.text.clear() }
                super.onPageStarted(view, url, favicon)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                urlShare = url
                super.onPageFinished(view, url)
            }
        }

    }

    private fun initListeners() {
        bGo.setOnClickListener { webview.go(this, etSearchQuery) }
        ibGoBack.setOnClickListener { webview.goBack() }
        ibGoForward.setOnClickListener { webview.goForward() }
        ibGoHome.setOnClickListener { webview.goHome() }
        ibRefresh.setOnClickListener { webview.refresh() }
        ibShare.setOnClickListener { share(urlShare) }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && this.webview.canGoBack()) {
            webview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun downloadDialog(url: String, userAgent: String, contentDisposition: String, mimetype: String) {

        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        AlertDialog.Builder(this).apply {
            setTitle("Download")
            setMessage("Do you want to save $filename")
            setPositiveButton("Yes") { _, _ ->

                val request = DownloadManager.Request(Uri.parse(url))
                val cookie = CookieManager.getInstance().getCookie(url)

                request.apply {
                    addRequestHeader("Cookie", cookie)
                    addRequestHeader("User-Agent", userAgent)
                    allowScanningByMediaScanner()
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                }

                val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                downloadmanager.enqueue(request)
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        }.create().show()

    }

    override fun onPause() {
        super.onPause()
        webview.onPause()
        webview.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        webview.onResume()
        webview.resumeTimers()
    }

}