package com.example.legalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.R
import com.example.legalapp.models.SubmittedForm
import com.google.android.material.button.MaterialButton

class FormsAdapter(
    private val forms: List<SubmittedForm>,
    private val onItemClick: (SubmittedForm) -> Unit
) : RecyclerView.Adapter<FormsAdapter.FormViewHolder>() {

    class FormViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val formTypeText: TextView = view.findViewById(R.id.formTypeText)
        val formTitleText: TextView = view.findViewById(R.id.formTitleText)
        val submissionDateText: TextView = view.findViewById(R.id.submissionDateText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val trackButton: MaterialButton = view.findViewById(R.id.trackButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submitted_form, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val form = forms[position]
        
        holder.formTypeText.text = form.type
        holder.formTitleText.text = form.title
        holder.submissionDateText.text = form.submissionDate
        holder.statusText.text = form.status

        holder.trackButton.setOnClickListener {
            onItemClick(form)
        }

        // Optional: Change status text color based on status
        holder.statusText.setTextColor(
            holder.itemView.context.getColor(
                when (form.status.lowercase()) {
                    "pending" -> R.color.warning
                    "approved" -> R.color.secondary_green
                    "rejected" -> R.color.error
                    else -> R.color.secondary_orange
                }
            )
        )
    }

    override fun getItemCount() = forms.size
} 