package com.example.legalapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.widget.Toast
import android.content.Intent
import android.util.Log

class NewApplicationActivity : AppCompatActivity() {
    private lateinit var micButton: FloatingActionButton
    private lateinit var recordingStatus: TextView
    private lateinit var nameInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var reliefInput: TextInputEditText
    private lateinit var progressBar: View
    
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_new_application)

            setupViews()
            setupSpeechRecognition()
            checkPermissions()
            setupFormBasedOnType()
        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing application", Toast.LENGTH_LONG).show()
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

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            submitApplication()
        }
    }

    private fun setupSpeechRecognition() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(this)) {
                Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_LONG).show()
                micButton.isEnabled = false
                return
            }
            
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isRecording = true
                    recordingStatus.text = "Listening..."
                    micButton.setImageResource(android.R.drawable.ic_media_pause)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        processVoiceInput(spokenText)
                    }
                    stopRecording()
                }

                // Implement other required methods...
                override fun onError(error: Int) {
                    stopRecording()
                    recordingStatus.text = "Error occurred. Please try again."
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } catch (e: Exception) {
            Log.e("NewApplicationActivity", "Error setting up speech recognition: ${e.message}", e)
            Toast.makeText(this, "Could not initialize speech recognition", Toast.LENGTH_SHORT).show()
            micButton.isEnabled = false
        }
    }

    private fun startRecording() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your legal issue...")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN") // For Indian English
        }
        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting speech recognition", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        isRecording = false
        speechRecognizer.stopListening()
        recordingStatus.text = "Tap to start recording"
        micButton.setImageResource(android.R.drawable.ic_btn_speak_now)
    }

    private fun processVoiceInput(spokenText: String) {
        progressBar.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call BHASHINI API for translation and NLP
                val bhashiniResponse = callBhashiniApi(spokenText)
                
                // Extract information from the processed text
                val extractedInfo = extractInformation(bhashiniResponse)
                
                // Update UI on main thread
                runOnUiThread {
                    nameInput.setText(extractedInfo.name)
                    descriptionInput.setText(extractedInfo.description)
                    reliefInput.setText(extractedInfo.relief)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    // Show error message
                }
            }
        }
    }

    private fun callBhashiniApi(text: String): String {
        // Implementation of BHASHINI API call
        // This is a placeholder - you'll need to implement the actual API call
        val url = URL("https://bhashini.gov.in/api/v1/inference/translation")
        val connection = url.openConnection() as HttpsURLConnection
        
        // Add your API configuration here
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "YOUR_API_KEY")
        
        val requestBody = JSONObject().apply {
            put("input_text", text)
            put("source_language", "auto")
            put("target_language", "en")
        }.toString()
        
        // Send request and get response
        connection.outputStream.write(requestBody.toByteArray())
        val response = connection.inputStream.bufferedReader().readText()
        
        return response
    }

    private fun extractInformation(apiResponse: String): ApplicationInfo {
        // Implement NLP to extract relevant information
        // This is a placeholder - you'll need to implement actual information extraction
        return ApplicationInfo(
            name = "Extracted Name",
            description = "Extracted Description",
            relief = "Extracted Relief"
        )
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
            findViewById<TextView>(R.id.toolbarTitle)?.text = formTitle

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
            // Don't finish the activity, just show an error message
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

    private fun submitApplication() {
        val name = nameInput.text.toString()
        val description = descriptionInput.text.toString()
        val relief = reliefInput.text.toString()
        val formType = intent.getStringExtra("form_type") ?: return

        if (name.isEmpty() || description.isEmpty() || relief.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(1500) // Simulate network call

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@NewApplicationActivity, 
                        "$formType submitted successfully", 
                        Toast.LENGTH_LONG
                    ).show()
                    
                    nameInput.text?.clear()
                    descriptionInput.text?.clear()
                    reliefInput.text?.clear()
                    
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@NewApplicationActivity, 
                        "Error submitting form: ${e.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
} 