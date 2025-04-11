package com.example.doctor

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HealthTipsActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_tips)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        val btnCold = findViewById<Button>(R.id.btnCold)
        val btnFever = findViewById<Button>(R.id.btnFever)
        val btnHeadache = findViewById<Button>(R.id.btnHeadache)
        val btnInjury = findViewById<Button>(R.id.btnInjury)

        // Set click listeners for each button to load a different YouTube video
        btnCold.setOnClickListener {
            loadVideo("https://youtube.com/shorts/UDHS9pR_SRY?si=LFaxlo-tuKWPXKa6") // Replace with a real URL
        }

        btnFever.setOnClickListener {
            loadVideo("https://www.youtube.com/embed/NSa-FXMc0xU") // Replace with a real URL
        }

        btnHeadache.setOnClickListener {
            loadVideo("https://www.youtube.com/embed/1z3onE3sZis") // Replace with a real URL
        }

        btnInjury.setOnClickListener {
            loadVideo("https://www.youtube.com/embed/t6uKsbWrFvY") // Replace with a real URL
        }
    }

    private fun loadVideo(videoUrl: String) {
        val embedUrl = "$videoUrl?autoplay=1"
        webView.loadUrl(embedUrl)
    }
}
