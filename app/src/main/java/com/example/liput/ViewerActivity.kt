package com.example.liput

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        val fileName = intent.getStringExtra("file") ?: return
        val file = File(filesDir, fileName)
        webView.loadUrl("file://${file.absolutePath}")
    }
}
