package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.example.legalapp.utils.SessionManager

class ProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        // Set up initial user data
        if (!sessionManager.isLoggedIn) {
            sessionManager.setUserLoggedIn("Vatsalya Bhadaurya")
        }

        setupViews()
    }

    private fun setupViews() {
        // Setup toolbar
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        // Set user name
        findViewById<TextView>(R.id.userNameText).text = sessionManager.userName

        // Setup logout button
        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout() {
        sessionManager.logout()
        // Navigate to MainActivity and clear the back stack
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
} 