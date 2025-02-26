package com.example.legalapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.legalapp.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputLayout
import android.widget.LinearLayout

class NewApplicationActivity : AppCompatActivity() {
    private lateinit var micButton: FloatingActionButton
    private lateinit var recordingStatus: TextView
    private lateinit var nameInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var reliefInput: TextInputEditText
    private lateinit var progressBar: View
    private lateinit var sessionManager: SessionManager
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isRecording = false
    private var currentEditText: EditText? = null
    private var formContainer: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_new_application)

            // Initialize views
            formContainer = findViewById(R.id.formContainer)
            micButton = findViewById(R.id.micButton)
            recordingStatus = findViewById(R.id.recordingStatus)
            progressBar = findViewById(R.id.progressBar)
            sessionManager = SessionManager(this)

            // Get form type from intent
            val formType = intent.getStringExtra("FORM_TYPE") ?: "GENERAL"
            val formTitle = intent.getStringExtra("FORM_TITLE") ?: "New Application"

            // Set up toolbar
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            // Set the title
            findViewById<TextView>(R.id.titleText)?.text = formTitle

            // Setup form fields based on type
            setupFormFields(formType)

            // Setup other functionality
            setupSpeechRecognition()
            checkPermissions()

        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing form: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupViews() {
        micButton = findViewById(R.id.micButton)
        recordingStatus = findViewById(R.id.recordingStatus)
        nameInput = findViewById(R.id.nameInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        reliefInput = findViewById(R.id.reliefInput)
        progressBar = findViewById(R.id.progressBar)

        micButton.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }

        findViewById<MaterialButton>(R.id.submitButton).setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun setupSpeechRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    currentEditText?.hint = "Listening..."
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    runOnUiThread {
                        currentEditText?.setText(spokenText)
                    }
                }
                stopVoiceRecognition()
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    else -> "Error occurred. Please try again"
                }
                showError(message)
                stopVoiceRecognition()
            }

            // Required overrides
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startRecording() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your legal issue...")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, sessionManager.userLanguage)
        }
        try {
            isRecording = true
            micButton.setImageResource(android.R.drawable.ic_media_pause)
            progressBar.visibility = View.VISIBLE
            recordingStatus.text = getString(R.string.tap_to_speak)

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    speechRecognizer.startListening(intent)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewApplicationActivity, "Error starting speech recognition", Toast.LENGTH_SHORT).show()
                        stopRecording()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error in startRecording: ${e.message}")
            Toast.makeText(this, "Error starting speech recognition", Toast.LENGTH_SHORT).show()
            stopRecording()
        }
    }

    private fun stopRecording() {
        isRecording = false
        micButton.setImageResource(android.R.drawable.ic_btn_speak_now)
        progressBar.visibility = View.GONE
        try {
            speechRecognizer.stopListening()
        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error stopping recording: ${e.message}")
        }
    }

    private fun processVoiceInput(spokenText: String) {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Simple text processing
                val processedText = spokenText.trim()
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    currentEditText?.setText(processedText)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@NewApplicationActivity,
                        "Error processing voice input: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setupFormBasedOnType() {
        try {
            val formType = intent.getStringExtra("form_type") ?: return
            val formTitle = intent.getStringExtra("form_title") ?: return
            val formDescription = intent.getStringExtra("form_description") ?: return

            // Update the title
            findViewById<TextView>(R.id.titleText)?.text = formTitle

            // Pre-fill form fields based on type
            when (formType) {
                "RTI-FORM" -> setupRTIForm()
                "DIV-FORM" -> setupDivorceForm()
                "DV-FORM" -> setupDomesticViolenceForm()
                "MARRIAGE-FORM" -> setupMarriageForm()
                "TENANT-FORM" -> setupTenantForm()
                "CONSUMER-FORM" -> setupConsumerForm()
                "MAINTENANCE-FORM" -> setupMaintenanceForm()
                "FIR-FORM" -> setupFIRForm()
                "WILL-FORM" -> setupWillForm()
                "LABOUR-FORM" -> setupLabourForm()
            }

            // Update description field with required documents
            descriptionInput.hint = "Details (Required: ${formDescription})"
        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error in setupFormBasedOnType: ${e.message}", e)
            Toast.makeText(this, "Error setting up form", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRTIForm() {
        nameInput.hint = "Applicant Full Name"
        reliefInput.hint = "Information Requested"
        // Add any RTI-specific fields
    }

    private fun setupDivorceForm() {
        nameInput.hint = "Both Parties' Names"
        reliefInput.hint = "Grounds for Divorce"
        // Add marriage date field, etc.
    }

    private fun setupDomesticViolenceForm() {
        nameInput.hint = "Complainant Name"
        reliefInput.hint = "Nature of Relief Sought"
        // Add incident details field
    }

    private fun setupMarriageForm() {
        nameInput.hint = "Names of Both Parties"
        reliefInput.hint = "Marriage Details"
        // Add witness details fields
    }

    private fun setupTenantForm() {
        nameInput.hint = "Tenant and Owner Names"
        reliefInput.hint = "Property and Rent Details"
        // Add property details fields
    }

    private fun setupConsumerForm() {
        nameInput.hint = "Consumer Name"
        reliefInput.hint = "Complaint Details and Relief Sought"
        // Add product/service details fields
    }

    private fun setupMaintenanceForm() {
        nameInput.hint = "Applicant Name"
        reliefInput.hint = "Maintenance Amount Requested"
        // Add income details fields
    }

    private fun setupFIRForm() {
        nameInput.hint = "Complainant Name"
        reliefInput.hint = "Incident Details"
        // Add incident location and time fields
    }

    private fun setupWillForm() {
        nameInput.hint = "Testator Name"
        reliefInput.hint = "Will Details"
        // Add beneficiary fields
    }

    private fun setupLabourForm() {
        nameInput.hint = "Employee Name"
        reliefInput.hint = "Complaint Details"
        // Add employer details fields
    }

    private fun setupSubmitButton() {
        findViewById<MaterialButton>(R.id.submitButton).setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun validateForm(): Boolean {
        val name = nameInput.text?.toString()?.trim() ?: ""
        val description = descriptionInput.text?.toString()?.trim() ?: ""
        val relief = reliefInput.text?.toString()?.trim() ?: ""

        if (name.isEmpty()) {
            nameInput.error = "Please enter your name"
            return false
        }

        if (description.isEmpty()) {
            descriptionInput.error = "Please provide case description"
            return false
        }

        if (relief.isEmpty()) {
            reliefInput.error = "Please specify the relief sought"
            return false
        }

        return true
    }

    private fun submitForm() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        findViewById<MaterialButton>(R.id.submitButton).isEnabled = false

        // Get form data
        val formData = hashMapOf(
            "applicant_name" to nameInput.text.toString().trim(),
            "description" to descriptionInput.text.toString().trim(),
            "relief_sought" to reliefInput.text.toString().trim(),
            "submission_date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            "status" to "Pending"
        )

        // Simulate API call with coroutines
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Simulate network delay
                delay(1500)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    showSuccessDialog()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    findViewById<MaterialButton>(R.id.submitButton).isEnabled = true
                    Toast.makeText(
                        this@NewApplicationActivity,
                        "Error submitting form: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("Your application has been submitted successfully!")
            .setPositiveButton("View My Applications") { _, _ ->
                startActivity(Intent(this, MyFormsActivity::class.java))
                finish()
            }
            .setNegativeButton("Back to Home") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, sessionManager.userLanguage)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        }
        speechRecognizer.startListening(intent)
    }

    private fun stopVoiceRecognition() {
        runOnUiThread {
            progressBar.visibility = View.GONE
            currentEditText?.hint = currentEditText?.hint?.toString()?.replace("Listening...", "") ?: ""
        }
    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            recordingStatus.text = message
            progressBar.visibility = View.GONE
            micButton.isEnabled = true
            currentEditText?.hint = currentEditText?.hint?.toString()?.replace("Listening...", "") ?: ""
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, setup speech recognition
                setupSpeechRecognition()
            } else {
                Toast.makeText(this, "Permission needed for voice input", Toast.LENGTH_SHORT).show()
                micButton.isEnabled = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    data class ApplicationInfo(
        val name: String,
        val description: String,
        val relief: String
    )

    private fun setupFormFields(formType: String) {
        try {
            // Clear existing fields first
            formContainer?.removeAllViews()

            // Add form fields based on type
            when (formType) {
                "RTI" -> {
                    addField("Department/Organization Name")
                    addField("Information Required")
                    addField("Time Period")
                    addField("Purpose")
                }
                "DIVORCE" -> {
                    addField("Spouse Name")
                    addField("Marriage Date")
                    addField("Reason for Divorce")
                    addField("Marriage Registration Number")
                }
                "FIR" -> {
                    addField("Incident Date")
                    addField("Incident Location")
                    addField("Description of Incident")
                    addField("Accused Details (if known)")
                }
                "PROPERTY" -> {
                    addField("Property Address")
                    addField("Property Type")
                    addField("Dispute Description")
                    addField("Other Party Details")
                }
                "CIVIL" -> {
                    addField("Case Title")
                    addField("Parties Involved")
                    addField("Case Description")
                    addField("Relief Sought")
                }
                "CRIMINAL" -> {
                    addField("Nature of Complaint")
                    addField("Incident Details")
                    addField("Accused Information")
                    addField("Witnesses (if any)")
                }
                else -> {
                    // Default fields
                    addField("Application Title")
                    addField("Description")
                    addField("Supporting Documents")
                }
            }

            // Add common fields
            addField("Additional Comments")
            addField("Contact Information")

            // Add submit button at the bottom
            addSubmitButton()

        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error in setupFormFields: ${e.message}", e)
            Toast.makeText(this, "Error setting up form fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addField(hint: String) {
        try {
            // Create horizontal layout
            val horizontalLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            // Create TextInputLayout with EditText
            val textInputLayout = TextInputLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,  // width will be determined by weight
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f  // takes up remaining space
                    marginEnd = 8
                }
                boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            }

            val editText = TextInputEditText(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                this.hint = hint
                setPadding(16, 16, 16, 16)
            }

            // Create voice input button
            val voiceButton = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    48,  // fixed width
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }
                setImageResource(R.drawable.ic_mic)  // make sure you have this drawable
                background = null  // removes button background
                setColorFilter(ContextCompat.getColor(context, R.color.primary))
                contentDescription = "Voice input for $hint"
                
                setOnClickListener {
                    currentEditText = editText
                    startVoiceRecognition()
                }
            }

            // Add views to their containers
            textInputLayout.addView(editText)
            horizontalLayout.addView(textInputLayout)
            horizontalLayout.addView(voiceButton)
            formContainer?.addView(horizontalLayout)

        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error adding field: ${e.message}", e)
        }
    }

    private fun addSubmitButton() {
        try {
            // Create submit button
            val submitButton = MaterialButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 32, 16, 16)
                }
                text = "Submit Application"
                setBackgroundColor(ContextCompat.getColor(context, R.color.secondary_green))
                setTextColor(ContextCompat.getColor(context, R.color.text_white))
                cornerRadius = resources.getDimensionPixelSize(R.dimen.button_corner_radius)
                elevation = resources.getDimensionPixelSize(R.dimen.button_elevation).toFloat()
                
                setOnClickListener {
                    if (validateForm()) {
                        submitForm()
                    }
                }
            }

            // Add button to form container
            formContainer?.addView(submitButton)

        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error adding submit button: ${e.message}", e)
        }
    }

    // Add this to handle back button in toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 