package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupLanguageSpinner()
        setupClickListeners()
        setupSearch()
        setupChatbot()
    }
    
    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "हिंदी", "తెలుగు", "தமிழ்", "ಕನ್ನಡ")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.languageSpinner).adapter = adapter
    }
    
    private fun setupClickListeners() {
        findViewById<CardView>(R.id.newApplicationCard).setOnClickListener {
            startActivity(Intent(this, NewApplicationActivity::class.java))
        }
        
        findViewById<CardView>(R.id.trackApplicationCard).setOnClickListener {
            startActivity(Intent(this, TrackApplicationActivity::class.java))
        }

        findViewById<CardView>(R.id.civilCard).setOnClickListener {
            Toast.makeText(this, "Civil Cases Selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<CardView>(R.id.criminalCard).setOnClickListener {
            Toast.makeText(this, "Criminal Cases Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearch() {
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val searchBar = findViewById<EditText>(R.id.searchBar)

        searchButton.setOnClickListener {
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(this, SearchResultsActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
            }
        }
    }

    private fun setupChatbot() {
        findViewById<FloatingActionButton>(R.id.chatbotFab).setOnClickListener {
            Toast.makeText(this, "AI Assistant Coming Soon", Toast.LENGTH_SHORT).show()
            // Launch chatbot activity
        }
    }
} 