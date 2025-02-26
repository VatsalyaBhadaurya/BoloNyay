package com.example.legalapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get saved user data
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val userName = prefs.getString("name", "John Doe")
        val userEmail = prefs.getString("email", "john.doe@example.com")
        val userPhone = prefs.getString("phone", "+91 9876543210")

        // Set user data to views
        findViewById<TextView>(R.id.userName).text = userName
        findViewById<TextView>(R.id.userEmail).text = userEmail
        findViewById<TextView>(R.id.userPhone).text = userPhone

        // Setup logout button
        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            // Handle logout
            // Clear preferences, go to login screen, etc.
        }
    }
} 