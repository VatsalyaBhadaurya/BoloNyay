package com.example.legalapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.legalapp.adapters.FormsAdapter
import com.example.legalapp.models.Form
import java.text.SimpleDateFormat
import java.util.*

class MyFormsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_forms)

        setupRecyclerView()
        setupToolbar()
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.formsRecyclerView)
        val emptyView = findViewById<TextView>(R.id.emptyView)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Sample data - Replace with actual data from your database
        val sampleForms = listOf(
            Form(
                id = "1",
                title = "RTI Application",
                type = "RTI",
                applicantName = "John Doe",
                description = "Information request about public works",
                relief = "Document access",
                submissionDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                status = "Pending"
            ),
            Form(
                id = "2",
                title = "Civil Case Filing",
                type = "Civil",
                applicantName = "John Doe",
                description = "Property dispute case",
                relief = "Property rights",
                submissionDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                status = "Under Review"
            )
        )

        recyclerView.adapter = FormsAdapter(sampleForms)
        
        // Show empty view if no forms
        emptyView.visibility = if (sampleForms.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (sampleForms.isEmpty()) View.GONE else View.VISIBLE
    }
} 