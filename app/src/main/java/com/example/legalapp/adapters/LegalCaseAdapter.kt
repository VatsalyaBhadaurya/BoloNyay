package com.example.legalapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.R
import com.example.legalapp.models.LegalCase
import com.google.android.material.button.MaterialButton
import com.example.legalapp.NewApplicationActivity

class LegalCaseAdapter(
    private val cases: List<LegalCase>,
    private val onItemClick: (LegalCase) -> Unit
) : RecyclerView.Adapter<LegalCaseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val caseTypeText: TextView = view.findViewById(R.id.caseTypeText)
        val caseTitleText: TextView = view.findViewById(R.id.caseTitleText)
        val caseDescriptionText: TextView = view.findViewById(R.id.caseDescriptionText)
        val viewDetailsButton: MaterialButton = view.findViewById(R.id.viewDetailsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_legal_case, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val case = cases[position]
        
        holder.caseTypeText.text = case.type
        holder.caseTitleText.text = case.title
        holder.caseDescriptionText.text = case.description

        holder.viewDetailsButton.setOnClickListener {
            onItemClick(case)
        }
    }

    override fun getItemCount() = cases.size
} 