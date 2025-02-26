package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.util.Log
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.legalapp.utils.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sessionManager = SessionManager(this)
        setupLanguageSpinner()
        setupClickListeners()
        setupBottomNavigation()
        setupSearch()
    }
    
    private fun setupClickListeners() {
        // Make cards clickable
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

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Just stay on home
                    true
                }
                R.id.navigation_forms -> {
                    startActivity(Intent(this, TrackApplicationActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearch() {
        val searchBar = findViewById<EditText>(R.id.searchBar)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val noResultsLayout = findViewById<View>(R.id.noResultsLayout)
        val mainContent = findViewById<ScrollView>(R.id.mainContent)

        // Initially hide no results layout
        noResultsLayout.visibility = View.GONE
        mainContent.visibility = View.VISIBLE

        searchButton.setOnClickListener {
            performSearch(searchBar.text.toString())
        }

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchBar.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter search terms", Toast.LENGTH_SHORT).show()
            return
        }

        val noResultsLayout = findViewById<View>(R.id.noResultsLayout)
        val mainContent = findViewById<ScrollView>(R.id.mainContent)

        // Define search keywords and their corresponding forms
        val searchResults = when (query.lowercase()) {
            in listOf("rti", "right to information", "information") -> {
                openNewApplication("RTI", "Right to Information Application")
                true
            }
            in listOf("divorce", "divorcee", "marriage") -> {
                openNewApplication("DIVORCE", "Divorce Application")
                true
            }
            in listOf("fir", "police complaint", "complaint") -> {
                openNewApplication("FIR", "First Information Report")
                true
            }
            in listOf("property", "land", "real estate", "dispute") -> {
                openNewApplication("PROPERTY", "Property Dispute Application")
                true
            }
            in listOf("civil", "civil case") -> {
                openNewApplication("CIVIL", "Civil Case Application")
                true
            }
            in listOf("criminal", "criminal case") -> {
                openNewApplication("CRIMINAL", "Criminal Case Application")
                true
            }
            else -> false
        }

        if (!searchResults) {
            // Show no results found message
            noResultsLayout.visibility = View.VISIBLE
            mainContent.visibility = View.GONE
            Toast.makeText(this, "No results found for '$query'", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNewApplication(formType: String, formTitle: String) {
        try {
            val intent = Intent(this, NewApplicationActivity::class.java).apply {
                putExtra("FORM_TYPE", formType)
                putExtra("FORM_TITLE", formTitle)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening form: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Error opening form", e)
        }
    }

    private fun setupLanguageSpinner() {
        val spinner = findViewById<Spinner>(R.id.languageSpinner)
        
        // Define languages
        val languages = arrayOf(
            "English",
            "हिंदी (Hindi)",
            "বাংলা (Bengali)",
            "मराठी (Marathi)",
            "தமிழ் (Tamil)",
            "తెలుగు (Telugu)",
            "ગુજરાતી (Gujarati)",
            "ಕನ್ನಡ (Kannada)",
            "മലയാളം (Malayalam)",
            "ਪੰਜਾਬੀ (Punjabi)"
        )

        // Create adapter
        val adapter = ArrayAdapter(this, R.layout.spinner_item, languages)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        
        // Set adapter
        spinner.adapter = adapter

        // Set selection based on saved language
        val savedLanguage = sessionManager.userLanguage
        val index = languages.indexOfFirst { it.contains(savedLanguage) }
        if (index != -1) {
            spinner.setSelection(index)
        }

        // Handle selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position].split(" ")[0]  // Get first part before space
                sessionManager.userLanguage = selectedLanguage
                updateUILanguage(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateUILanguage(language: String) {
        // Update UI text based on selected language
        // This is where you would update all text in the UI
        val searchBar = findViewById<EditText>(R.id.searchBar)
        when (language) {
            "हिंदी" -> {
                searchBar.hint = "कानूनी सहायता खोजें..."
                // Update other UI elements
            }
            "বাংলা" -> {
                searchBar.hint = "আইনি সহায়তা খুঁজুন..."
                // Update other UI elements
            }
            // Add cases for other languages
            else -> {
                searchBar.hint = "Search legal aid..."
                // Reset to English
            }
        }
    }
} 