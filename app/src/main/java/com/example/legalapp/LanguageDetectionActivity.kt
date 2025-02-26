package com.example.legalapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.legalapp.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LanguageDetectionActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var micButton: FloatingActionButton
    private lateinit var statusText: TextView
    private lateinit var continueButton: MaterialButton

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_detection)

        sessionManager = SessionManager(this)
        setupViews()
        setupSpeechRecognizer()
        checkPermission()
    }

    private fun setupViews() {
        micButton = findViewById(R.id.micButton)
        statusText = findViewById(R.id.statusText)
        continueButton = findViewById(R.id.continueButton)

        micButton.setOnClickListener {
            startVoiceRecognition()
        }

        continueButton.setOnClickListener {
            proceedToMainActivity()
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                statusText.text = "Listening..."
                micButton.isEnabled = false
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    statusText.text = "Processing: $spokenText"
                    detectLanguage(spokenText)
                } else {
                    statusText.text = "Could not detect speech. Please try again."
                }
                micButton.isEnabled = true
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Error occurred. Try again"
                }
                statusText.text = errorMessage
                micButton.isEnabled = true
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    statusText.text = "Heard: ${matches[0]}"
                }
            }

            // Other required methods...
            override fun onBeginningOfSpeech() {
                statusText.text = "Listening..."
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                statusText.text = "Processing..."
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // Add support for multiple languages
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN,ta-IN,te-IN,kn-IN,en-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        speechRecognizer.startListening(intent)
    }

    private fun detectLanguage(spokenText: String) {
        // Improved language detection with more patterns
        val languageCode = when {
            // Hindi detection
            spokenText.matches(Regex(".*[अ-ह़].*")) -> "hi"
            // Tamil detection
            spokenText.matches(Regex(".*[அ-ஔ].*|.*[க-ன].*")) -> "ta"
            // Telugu detection
            spokenText.matches(Regex(".*[అ-ఱ].*|.*[ౠ-ౡ].*")) -> "te"
            // Kannada detection
            spokenText.matches(Regex(".*[ಅ-ಹ].*|.*[ೞ].*")) -> "kn"
            else -> "en"
        }

        sessionManager.userLanguage = languageCode
        updateUIForDetectedLanguage(languageCode)
    }

    private fun updateUIForDetectedLanguage(languageCode: String) {
        val languageName = when (languageCode) {
            "hi" -> "Hindi (हिंदी)"
            "ta" -> "Tamil (தமிழ்)"
            "te" -> "Telugu (తెలుగు)"
            "kn" -> "Kannada (ಕನ್ನಡ)"
            else -> "English"
        }
        statusText.text = "Detected language: $languageName"
        continueButton.visibility = View.VISIBLE
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun proceedToMainActivity() {
        sessionManager.isFirstTime = false
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
} 