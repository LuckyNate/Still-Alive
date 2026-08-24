package com.prankdom.stillalive

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterFullscreen()

        webView = WebView(this).apply {
            setBackgroundColor(0xFF000000.toInt())
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = false
            addJavascriptInterface(ShareBridge(), "StillAliveAndroid")
            loadUrl("file:///android_asset/index.html")
        }

        setContentView(webView)
    }

    inner class ShareBridge {
        @JavascriptInterface
        fun shareAlive() {
            runOnUiThread {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "I have confirmed that I am still alive.")
                }
                startActivity(Intent.createChooser(intent, "Let your friends know"))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enterFullscreen()
    }

    @Suppress("DEPRECATION")
    private fun enterFullscreen() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Intentionally inert: Still Alive has nowhere else to go.
    }
}
