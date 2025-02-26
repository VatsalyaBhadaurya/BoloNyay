package com.example.legalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.legalapp.R
import com.example.legalapp.models.Form

class FormsAdapter(private val forms: List<Form>) : 
    RecyclerView.Adapter<FormsAdapter.FormViewHolder>() {

    class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.formTitle)
        private val typeText: TextView = itemView.findViewById(R.id.formType)
        private val dateText: TextView = itemView.findViewById(R.id.formDate)
        private val statusText: TextView = itemView.findViewById(R.id.formStatus)

        fun bind(form: Form) {
            titleText.text = form.title
            typeText.text = form.type
            dateText.text = "Submitted on ${form.submissionDate}"
            statusText.text = "Status: ${form.status}"

            itemView.setOnClickListener {
                // Handle click to show form details
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submitted_form, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        holder.bind(forms[position])
    }

    override fun getItemCount() = forms.size
} 