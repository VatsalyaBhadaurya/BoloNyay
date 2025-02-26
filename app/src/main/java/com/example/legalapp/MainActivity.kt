package com.example.legalapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.legalapp.utils.LocaleHelper
import java.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.legalapp.utils.SessionManager

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        if (sessionManager.isFirstTime) {
            startActivity(Intent(this, LanguageDetectionActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        setupLanguageSpinner()
        setupClickListeners()
        setupSearch()
        setupChatbot()
        setupBottomNavigation()
    }
    
    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "हिंदी", "తెలుగు", "தமிழ்", "ಕನ್ನಡ")
        val languageCodes = arrayOf("en", "hi", "te", "ta", "kn") // ISO codes
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        val spinner = findViewById<Spinner>(R.id.languageSpinner)
        spinner.adapter = adapter

        // Set initial selection based on saved preference
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val currentLang = prefs.getString("language", "en") ?: "en"
        val currentIndex = languageCodes.indexOf(currentLang)
        if (currentIndex >= 0) {
            spinner.setSelection(currentIndex, false)
        }
        
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = languageCodes[position]
                val currentLanguage = LocaleHelper.getLanguage(this@MainActivity)
                
                // Only change language if it's different from current
                if (selectedLanguageCode != currentLanguage) {
                    changeAppLanguage(selectedLanguageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun changeAppLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // Save selected language
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        prefs.edit().putString("language", languageCode).apply()
        
        // Recreate activity once
        recreate()
    }
    
    private fun setupClickListeners() {
        // Make cards clickable
        findViewById<CardView>(R.id.newApplicationCard).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                try {
                    startActivity(Intent(this@MainActivity, NewApplicationActivity::class.java))
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting NewApplicationActivity: ${e.message}")
                    Toast.makeText(this@MainActivity, "Error opening form", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        findViewById<CardView>(R.id.trackApplicationCard).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                try {
                    startActivity(Intent(this@MainActivity, TrackApplicationActivity::class.java))
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting TrackApplicationActivity: ${e.message}")
                    Toast.makeText(this@MainActivity, "Error opening tracking", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<CardView>(R.id.civilCard).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Civil Cases Selected", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<CardView>(R.id.criminalCard).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Criminal Cases Selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Make search clickable
        findViewById<ImageButton>(R.id.searchButton).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val searchBar = findViewById<EditText>(R.id.searchBar)
                val query = searchBar.text.toString()
                if (query.isNotEmpty()) {
                    try {
                        val intent = Intent(this@MainActivity, SearchResultsActivity::class.java)
                        intent.putExtra("query", query)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error starting SearchResultsActivity: ${e.message}")
                        Toast.makeText(this@MainActivity, "Error performing search", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Make chatbot FAB clickable
        findViewById<FloatingActionButton>(R.id.chatbotFab).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                try {
                    startActivity(Intent(this@MainActivity, ChatbotActivity::class.java))
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting ChatbotActivity: ${e.message}")
                    Toast.makeText(this@MainActivity, "Error opening chatbot", Toast.LENGTH_SHORT).show()
                }
            }
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
            startActivity(Intent(this, ChatbotActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Show home content
                    true
                }
                R.id.navigation_forms -> {
                    // Show forms list
                    startActivity(Intent(this, MyFormsActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    // Show profile
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
} 