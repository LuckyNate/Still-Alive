package com.prankdom.stillalive

import android.app.Activity
import android.content.ClipData
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
        fun shareAlive(previousConfirmedAt: Long) {
            runOnUiThread {
                val confirmation = if (previousConfirmedAt <= 0L) {
                    "For the first time, I have confirmed that I am still alive."
                } else {
                    "After ${formatElapsed(previousConfirmedAt)}, I have confirmed that I am still alive."
                }

                val shareDir = File(cacheDir, "shares").apply { mkdirs() }
                val imageFile = File(shareDir, "still-alive.png")
                createAliveStoryImage(imageFile, confirmation)

                val imageUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "${packageName}.fileprovider",
                    imageFile
                )

                val caption = "$confirmation\n\nAre you STILL ALIVE?\nFind out today\nand let your friends know\nwith our STILL ALIVE app!\n100% FREE"

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, caption)
                    clipData = ClipData.newUri(contentResolver, "Still Alive", imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Share the good news").apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(chooser)
            }
        }
    }

    private fun formatElapsed(previousConfirmedAt: Long): String {
        var seconds = ((System.currentTimeMillis() - previousConfirmedAt) / 1000L).coerceAtLeast(0L)
        val days = seconds / 86400L
        seconds %= 86400L
        val hours = seconds / 3600L
        seconds %= 3600L
        val minutes = seconds / 60L
        seconds %= 60L

        fun unit(value: Long, name: String) = "$value $name${if (value == 1L) "" else "s"}"
        return listOf(
            unit(days, "day"),
            unit(hours, "hour"),
            unit(minutes, "minute"),
            unit(seconds, "second")
        ).joinToString(" ")
    }

    private fun createAliveStoryImage(file: File, confirmation: String) {
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
        val copyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 54f
        }
        val promoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 46f
        }

        val aliveY = height / 2f - 120f
        canvas.drawText("ALIVE", width / 2f, aliveY, alivePaint)

        drawWrappedCentered(canvas, confirmation, copyPaint, width / 2f, aliveY + 150f, 900f, 70f)
        drawWrappedCentered(
            canvas,
            "Are you STILL ALIVE? Find out today and let your friends know with our STILL ALIVE app! 100% FREE",
            promoPaint,
            width / 2f,
            height - 330f,
            900f,
            62f
        )

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()
    }

    private fun drawWrappedCentered(
        canvas: Canvas,
        text: String,
        paint: Paint,
        centerX: Float,
        startY: Float,
        maxWidth: Float,
        lineHeight: Float
    ) {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var line = ""

        for (word in words) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) <= maxWidth) {
                line = candidate
            } else {
                if (line.isNotEmpty()) lines.add(line)
                line = word
            }
        }
        if (line.isNotEmpty()) lines.add(line)

        lines.forEachIndexed { index, value ->
            canvas.drawText(value, centerX, startY + index * lineHeight, paint)
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
