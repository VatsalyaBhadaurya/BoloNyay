package com.example.legalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.example.legalapp.adapters.LegalCaseAdapter
import com.example.legalapp.models.LegalCase
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var noResultsText: TextView
    private lateinit var adapter: LegalCaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        setupViews()
        
        // Get search query from intent
        val searchQuery = intent.getStringExtra("search_query") ?: ""
        loadSearchResults(searchQuery)
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        noResultsText = findViewById(R.id.noResultsText)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup toolbar back button
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadSearchResults(query: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        noResultsText.visibility = View.GONE

        // Sample data - replace with actual search results
        val allCases = listOf(
            LegalCase(
                type = "Civil Case",
                title = "Property Rights",
                description = "File a case regarding property disputes and ownership claims"
            ),
            LegalCase(
                type = "RTI Application",
                title = "Information Request",
                description = "Request information from government departments under RTI Act"
            ),
            LegalCase(
                type = "Consumer Complaint",
                title = "Consumer Protection",
                description = "File complaints against unfair trade practices or defective products"
            ),
            LegalCase(
                type = "Family Case",
                title = "Divorce Petition",
                description = "File for divorce or separation under family law"
            ),
            LegalCase(
                type = "Criminal Case",
                title = "FIR Filing",
                description = "File a First Information Report for criminal offenses"
            )
        )

        // Filter cases based on search query
        val filteredCases = if (query.isNotEmpty()) {
            allCases.filter { case ->
                case.type.contains(query, ignoreCase = true) ||
                case.title.contains(query, ignoreCase = true) ||
                case.description.contains(query, ignoreCase = true)
            }
        } else {
            allCases
        }

        // Update UI based on results
        progressBar.visibility = View.GONE
        
        if (filteredCases.isEmpty()) {
            noResultsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            noResultsText.text = "No results found for '$query'"
        } else {
            noResultsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            
            adapter = LegalCaseAdapter(filteredCases) { case ->
                startActivity(Intent(this, NewApplicationActivity::class.java).apply {
                    putExtra("form_type", case.type)
                    putExtra("form_title", case.title)
                    putExtra("form_description", case.description)
                })
            }
            recyclerView.adapter = adapter
        }
    }
} 