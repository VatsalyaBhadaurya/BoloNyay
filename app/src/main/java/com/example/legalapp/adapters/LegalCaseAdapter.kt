package com.example.legalapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.R
import com.example.legalapp.models.LegalCase
import com.example.legalapp.NewApplicationActivity

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
            courtText.text = "Authority: ${legalCase.court}"
            statusText.text = "Required Documents Listed Below"

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NewApplicationActivity::class.java).apply {
                    putExtra("form_type", legalCase.caseNumber)
                    putExtra("form_title", legalCase.title)
                    putExtra("form_description", legalCase.description)
                }
                context.startActivity(intent)
            }
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