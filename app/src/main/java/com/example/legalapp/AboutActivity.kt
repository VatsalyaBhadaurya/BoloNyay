package com.example.legalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Setup toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup social links
        findViewById<ImageButton>(R.id.linkedinButton).setOnClickListener {
            openUrl("https://www.linkedin.com/in/vatsalya-bhadaurya")
        }

        findViewById<ImageButton>(R.id.githubButton).setOnClickListener {
            openUrl("https://github.com/VatsalyaBhadaurya")
        }

        findViewById<ImageButton>(R.id.websiteButton).setOnClickListener {
            openUrl("https://vatsalyaaa.me/")
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}