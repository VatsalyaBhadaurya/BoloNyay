package com.example.legalapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class TrackApplicationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_application)
        
        setupToolbar()
        setupRecyclerView()
        loadApplications()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.applicationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ApplicationAdapter(getSampleApplications())
    }

    private fun loadApplications() {
        // In a real app, this would load from database/API
        (recyclerView.adapter as ApplicationAdapter).updateApplications(getSampleApplications())
    }

    private fun getSampleApplications(): List<Application> {
        return listOf(
            Application(
                id = "12345",
                applicantName = "Vatsalya Bhadaurya",
                description = "Civil case regarding property dispute in Lucknow",
                submissionDate = "01/03/2024",
                status = "Pending"
            ),
            Application(
                id = "12346",
                applicantName = "Vatsalya Bhadaurya",
                description = "Criminal case for cyber fraud investigation",
                submissionDate = "28/02/2024",
                status = "In Progress"
            ),
            Application(
                id = "12347",
                applicantName = "Vatsalya Bhadaurya",
                description = "Family court matter regarding child custody",
                submissionDate = "25/02/2024",
                status = "Completed"
            ),
            Application(
                id = "12348",
                applicantName = "Vatsalya Bhadaurya",
                description = "Consumer court complaint against faulty product",
                submissionDate = "20/02/2024",
                status = "Rejected"
            )
        )
    }
}

data class Application(
    val id: String,
    val applicantName: String,
    val description: String,
    val submissionDate: String,
    val status: String
)

class ApplicationAdapter(
    private var applications: List<Application>
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(applications[position])
    }

    override fun getItemCount() = applications.size

    fun updateApplications(newApplications: List<Application>) {
        applications = newApplications
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val applicationId: TextView = view.findViewById(R.id.applicationId)
        private val applicantName: TextView = view.findViewById(R.id.applicantName)
        private val description: TextView = view.findViewById(R.id.description)
        private val submissionDate: TextView = view.findViewById(R.id.submissionDate)
        private val statusChip: Chip = view.findViewById(R.id.statusChip)

        fun bind(application: Application) {
            applicationId.text = "Application #${application.id}"
            applicantName.text = application.applicantName
            description.text = application.description
            submissionDate.text = "Submitted on: ${application.submissionDate}"
            
            statusChip.text = application.status
            val chipColor = when (application.status) {
                "Pending" -> R.color.secondary_orange
                "In Progress" -> R.color.primary
                "Completed" -> R.color.secondary_green
                "Rejected" -> R.color.error
                else -> R.color.text_secondary
            }
            statusChip.setChipBackgroundColorResource(chipColor)
        }
    }
} 