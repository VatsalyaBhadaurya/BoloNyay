package com.example.legalapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.legalapp.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class LanguageDetectionActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var micButton: FloatingActionButton
    private lateinit var statusText: TextView
    private lateinit var continueButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val PERMISSION_REQUEST_CODE = 123
    private var isListening = false

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
        progressBar = findViewById(R.id.progressBar)

        micButton.setOnClickListener {
            if (!isListening) {
                startVoiceRecognition()
            } else {
                stopVoiceRecognition()
            }
        }

        continueButton.setOnClickListener {
            proceedToMainActivity()
        }
    }

    private fun startVoiceRecognition() {
        try {
            isListening = true
            micButton.setImageResource(android.R.drawable.ic_media_pause)
            progressBar.visibility = View.VISIBLE
            statusText.text = getString(R.string.tap_to_speak)

            // Release previous instance
            if (::speechRecognizer.isInitialized) {
                speechRecognizer.destroy()
            }

            // Create new instance
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            setupRecognitionListener()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                // Support multiple languages
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN,ta-IN,te-IN,kn-IN,en-IN")
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("hi-IN", "ta-IN", "te-IN", "kn-IN", "en-IN"))
            }

            Handler(Looper.getMainLooper()).postDelayed({
                speechRecognizer.startListening(intent)
            }, 100)

        } catch (e: Exception) {
            Log.e("LanguageDetection", "Error: ${e.message}")
            stopVoiceRecognition()
            showError("Error starting speech recognition")
        }
    }

    private fun setupRecognitionListener() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    statusText.text = "Listening..."
                    progressBar.visibility = View.VISIBLE
                    micButton.isEnabled = true
                }
            }

            override fun onResults(results: Bundle?) {
                try {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        Log.d("LanguageDetection", "Received text: $spokenText")
                        detectLanguage(spokenText)
                    }
                } catch (e: Exception) {
                    Log.e("LanguageDetection", "Error processing results: ${e.message}")
                    showError("Error processing speech")
                } finally {
                    runOnUiThread {
                        micButton.isEnabled = true
                        stopVoiceRecognition()
                    }
                }
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        // Restart recognition if no match found
                        Handler(Looper.getMainLooper()).postDelayed({
                            startVoiceRecognition()
                        }, 100)
                        "No speech detected, trying again..."
                    }
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_CLIENT -> {
                        // Restart recognition on client error
                        Handler(Looper.getMainLooper()).postDelayed({
                            startVoiceRecognition()
                        }, 100)
                        "Restarting recognition..."
                    }
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission needed"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        stopVoiceRecognition()
                        Handler(Looper.getMainLooper()).postDelayed({
                            startVoiceRecognition()
                        }, 100)
                        "Service busy, retrying..."
                    }
                    else -> "Error occurred. Please try again"
                }
                showError(message)
            }

            override fun onEndOfSpeech() {
                runOnUiThread {
                    statusText.text = "Processing..."
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun stopVoiceRecognition() {
        try {
            isListening = false
            runOnUiThread {
                micButton.setImageResource(android.R.drawable.ic_btn_speak_now)
                progressBar.visibility = View.GONE
                micButton.isEnabled = true
            }
            if (::speechRecognizer.isInitialized) {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
            }
        } catch (e: Exception) {
            Log.e("LanguageDetection", "Error stopping recognition: ${e.message}")
        }
    }

    private fun showError(message: String) {
        runOnUiThread {
            statusText.text = message
            progressBar.visibility = View.GONE
            micButton.isEnabled = true
        }
    }

    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            showError("Speech recognition not available")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    statusText.text = "Listening..."
                    progressBar.visibility = View.VISIBLE
                    micButton.isEnabled = false
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    Log.d("LanguageDetection", "Received text: $spokenText")
                    detectLanguage(spokenText)
                }
                runOnUiThread {
                    micButton.isEnabled = true
                    stopVoiceRecognition()
                }
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission needed"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Service is busy"
                    else -> "Error occurred. Please try again"
                }
                showError(message)
                runOnUiThread {
                    micButton.isEnabled = true
                    stopVoiceRecognition()
                }
            }

            override fun onEndOfSpeech() {
                runOnUiThread {
                    statusText.text = "Processing..."
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun detectLanguage(text: String) {
        Log.d("LanguageDetection", "Detecting language for: $text")
        
        // More comprehensive language patterns
        val hindiPattern = ".*[अ-ह़ऱ-ॿ].*".toRegex()
        val tamilPattern = ".*[அ-ஔக-சஜ-டண-தந-னப-மய-வஷ-ஹ].*".toRegex()
        val teluguPattern = ".*[అ-ఌఎ-ఐఒ-నప-ళవ-హ].*".toRegex()
        val kannadaPattern = ".*[ಅ-ಌಎ-ಐಒ-ನಪ-ಳವ-ಹ].*".toRegex()

        // Common words in each language
        val hindiWords = listOf("नमस्ते", "धन्यवाद", "हाँ", "नहीं")
        val tamilWords = listOf("வணக்கம்", "நன்றி", "ஆம்", "இல்லை")
        val teluguWords = listOf("నమస్కారం", "ధన్యవాదాలు", "అవును", "కాదు")
        val kannadaWords = listOf("ನಮಸ್ಕಾರ", "ಧನ್ಯವಾದಗಳು", "ಹೌದು", "ಇಲ್ಲ")

        val languageCode = when {
            // Check for script patterns
            hindiPattern.matches(text) || hindiWords.any { text.contains(it, ignoreCase = true) } -> {
                Log.d("LanguageDetection", "Detected Hindi")
                "hi"
            }
            tamilPattern.matches(text) || tamilWords.any { text.contains(it, ignoreCase = true) } -> {
                Log.d("LanguageDetection", "Detected Tamil")
                "ta"
            }
            teluguPattern.matches(text) || teluguWords.any { text.contains(it, ignoreCase = true) } -> {
                Log.d("LanguageDetection", "Detected Telugu")
                "te"
            }
            kannadaPattern.matches(text) || kannadaWords.any { text.contains(it, ignoreCase = true) } -> {
                Log.d("LanguageDetection", "Detected Kannada")
                "kn"
            }
            else -> {
                Log.d("LanguageDetection", "Defaulting to English")
                "en"
            }
        }

        sessionManager.userLanguage = languageCode
        updateUIForDetectedLanguage(languageCode)
    }

    private fun updateUIForDetectedLanguage(languageCode: String) {
        val languageName = when (languageCode) {
            "hi" -> "हिंदी (Hindi)"
            "ta" -> "தமிழ் (Tamil)"
            "te" -> "తెలుగు (Telugu)"
            "kn" -> "ಕನ್ನಡ (Kannada)"
            else -> "English"
        }
        
        runOnUiThread {
            statusText.text = "Detected language: $languageName"
            continueButton.apply {
                visibility = View.VISIBLE
                setBackgroundColor(getColor(R.color.secondary_green))
                setTextColor(getColor(R.color.text_white))
            }
            micButton.setImageResource(android.R.drawable.ic_btn_speak_now)
        }
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
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }
} 