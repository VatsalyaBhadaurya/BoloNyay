package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.legalapp.adapters.FormsAdapter
import com.example.legalapp.models.Form
import com.example.legalapp.models.SubmittedForm
import java.text.SimpleDateFormat
import java.util.*

class MyFormsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FormsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_forms)

        setupViews()
        loadForms()
        setupToolbar()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.formsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadForms() {
        // Sample data - replace with actual data loading
        val sampleForms = listOf(
            SubmittedForm(
                type = "Civil Case",
                title = "Property Dispute",
                submissionDate = "2024-02-20",
                status = "Pending"
            ),
            SubmittedForm(
                type = "RTI Application",
                title = "Public Records Request",
                submissionDate = "2024-02-18",
                status = "Approved"
            ),
            // Add more sample forms as needed
        )

        adapter = FormsAdapter(sampleForms) { form ->
            // Handle form click
            startActivity(Intent(this, TrackApplicationActivity::class.java).apply {
                putExtra("form_type", form.type)
                putExtra("form_title", form.title)
                putExtra("form_status", form.status)
            })
        }
        recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }
    }
} 