package com.example.legalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.R
import com.example.legalapp.models.LegalCase

class LegalCaseAdapter : ListAdapter<LegalCase, LegalCaseAdapter.LegalCaseViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegalCaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_legal_case, parent, false)
        return LegalCaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: LegalCaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LegalCaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val caseNumberText: TextView = itemView.findViewById(R.id.caseNumberText)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val courtText: TextView = itemView.findViewById(R.id.courtText)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)

        fun bind(legalCase: LegalCase) {
            caseNumberText.text = legalCase.caseNumber
            titleText.text = legalCase.title
            courtText.text = legalCase.court
            statusText.text = legalCase.status
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LegalCase>() {
            override fun areItemsTheSame(oldItem: LegalCase, newItem: LegalCase): Boolean {
                return oldItem.caseNumber == newItem.caseNumber
            }

            override fun areContentsTheSame(oldItem: LegalCase, newItem: LegalCase): Boolean {
                return oldItem == newItem
            }
        }
    }
} 