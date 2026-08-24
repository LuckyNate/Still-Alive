package com.prankdom.stillalive

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

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
        fun shareAlive(previousElapsed: String) {
            runOnUiThread {
                val shareDir = File(cacheDir, "shares").apply { mkdirs() }
                val imageFile = File(shareDir, "still-alive.png")
                createAliveStoryImage(imageFile)

                val imageUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "${packageName}.fileprovider",
                    imageFile
                )

                val caption = if (previousElapsed.isBlank() || previousElapsed == "UNCONFIRMED") {
                    "For the first time, I have confirmed that I am still alive."
                } else {
                    "After $previousElapsed, I have confirmed that I am still alive."
                }

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, caption)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(intent, "Share the good news"))
            }
        }
    }

    private fun createAliveStoryImage(file: File) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val alivePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 250f
        }
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 72f
        }

        val aliveY = height / 2f - (alivePaint.descent() + alivePaint.ascent()) / 2f
        canvas.drawText("ALIVE", width / 2f, aliveY, alivePaint)
        canvas.drawText("STILL ALIVE", width / 2f, height - 140f, brandPaint)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()
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
