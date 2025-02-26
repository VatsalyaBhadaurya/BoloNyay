package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.adapters.LegalCaseAdapter
import com.example.legalapp.models.LegalCase
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var noResultsText: TextView
    private lateinit var adapter: LegalCaseAdapter

    // Sample data - simulating a database of cases
    private val allApplications = listOf(
        LegalCase(
            "RTI-FORM",
            "Right to Information (RTI) Application Form",
            "Information Commission",
            "Public Information",
            "Standard Form",
            "Ready to Fill",
            "File RTI application to any government department. Required documents: ID proof, Address proof"
        ),
        LegalCase(
            "DIV-FORM",
            "Mutual Consent Divorce Application",
            "Family Court",
            "Family Law",
            "Form 1",
            "Ready to Fill",
            "Application for divorce by mutual consent. Required: Marriage certificate, Photos, Address proof"
        ),
        LegalCase(
            "DV-FORM",
            "Domestic Violence Complaint Form",
            "Magistrate Court",
            "Protection Law",
            "Form DIR-I",
            "Ready to Fill",
            "File domestic violence complaint. Required: ID proof, Incident details, Address proof"
        ),
        LegalCase(
            "MARRIAGE-FORM",
            "Marriage Registration Application",
            "Marriage Registrar",
            "Family Law",
            "Form II",
            "Ready to Fill",
            "Register your marriage. Required: Age proof, Photos, Witness details, Address proof"
        ),
        LegalCase(
            "TENANT-FORM",
            "Tenant Registration Form",
            "Housing Authority",
            "Property Law",
            "Form C",
            "Ready to Fill",
            "Register tenant agreement. Required: Property papers, ID proofs of both parties"
        ),
        LegalCase(
            "CONSUMER-FORM",
            "Consumer Complaint Form",
            "Consumer Forum",
            "Consumer Law",
            "Form A",
            "Ready to Fill",
            "File consumer complaint. Required: Purchase bill, Correspondence with seller"
        ),
        LegalCase(
            "MAINTENANCE-FORM",
            "Maintenance Application Form",
            "Family Court",
            "Family Law",
            "Form III",
            "Ready to Fill",
            "Apply for maintenance under Section 125 CrPC. Required: Marriage proof, Income details"
        ),
        LegalCase(
            "FIR-FORM",
            "First Information Report (FIR)",
            "Police Station",
            "Criminal Law",
            "Standard Form",
            "Ready to Fill",
            "File police complaint. Required: ID proof, Incident details"
        ),
        LegalCase(
            "WILL-FORM",
            "Will Registration Form",
            "Sub-Registrar Office",
            "Property Law",
            "Form W",
            "Ready to Fill",
            "Register your will. Required: Property details, Witness details, ID proof"
        ),
        LegalCase(
            "LABOUR-FORM",
            "Labour Complaint Form",
            "Labour Commissioner",
            "Labour Law",
            "Form B",
            "Ready to Fill",
            "File workplace complaint. Required: Employment proof, Complaint details"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        noResultsText = findViewById(R.id.noResultsText)

        setupRecyclerView()
        
        val query = intent.getStringExtra("query") ?: return
        searchCases(query)
    }

    private fun setupRecyclerView() {
        adapter = LegalCaseAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun searchCases(query: String) {
        progressBar.visibility = View.VISIBLE
        noResultsText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                // Simulate network delay
                kotlinx.coroutines.delay(1000)

                // Filter applications based on search query
                val results = allApplications.filter { application ->
                    val searchTerms = query.lowercase().split(" ")
                    searchTerms.any { term ->
                        application.title.lowercase().contains(term) ||
                        application.description.lowercase().contains(term) ||
                        application.category.lowercase().contains(term)
                    }
                }.sortedBy { application -> 
                    if (application.title.lowercase().contains(query.lowercase())) 0 else 1
                }
                
                progressBar.visibility = View.GONE
                if (results.isEmpty()) {
                    noResultsText.visibility = View.VISIBLE
                    noResultsText.text = "No application forms found matching '$query'\nTry searching with different keywords"
                } else {
                    adapter.submitList(results)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                noResultsText.text = "Error: ${e.message}"
                noResultsText.visibility = View.VISIBLE
            }
        }
    }
} 