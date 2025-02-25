package com.example.legalapp

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
    private val allCases = listOf(
        LegalCase(
            "WP(C) 12345/2023",
            "Environmental Protection PIL",
            "High Court of Delhi",
            "Civil",
            "2023-12-01",
            "Pending",
            "PIL regarding air pollution in Delhi NCR"
        ),
        LegalCase(
            "CRL.A. 567/2023",
            "State vs Kumar Criminal Appeal",
            "Supreme Court of India",
            "Criminal",
            "2023-11-15",
            "Active",
            "Criminal appeal against High Court judgment"
        ),
        LegalCase(
            "CS(COMM) 789/2023",
            "Property Dispute Case",
            "District Court, Mumbai",
            "Civil",
            "2023-10-20",
            "Under Review",
            "Commercial property dispute in Mumbai"
        ),
        LegalCase(
            "WP(C) 234/2023",
            "Right to Education PIL",
            "High Court of Karnataka",
            "Civil",
            "2023-09-05",
            "Disposed",
            "PIL regarding implementation of RTE Act"
        ),
        LegalCase(
            "CWP 890/2023",
            "Labor Rights Petition",
            "High Court of Punjab & Haryana",
            "Civil",
            "2023-08-15",
            "Pending",
            "Petition regarding factory workers' rights"
        ),
        LegalCase(
            "SLP(Crl) 456/2023",
            "Special Leave Petition",
            "Supreme Court of India",
            "Criminal",
            "2023-07-30",
            "Listed",
            "Special leave to appeal against HC order"
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

                // Filter cases based on search query
                val results = allCases.filter { case ->
                    case.title.contains(query, ignoreCase = true) ||
                    case.caseNumber.contains(query, ignoreCase = true) ||
                    case.court.contains(query, ignoreCase = true) ||
                    case.description.contains(query, ignoreCase = true) ||
                    case.category.contains(query, ignoreCase = true)
                }
                
                progressBar.visibility = View.GONE
                if (results.isEmpty()) {
                    noResultsText.visibility = View.VISIBLE
                    noResultsText.text = "No cases found matching '$query'"
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