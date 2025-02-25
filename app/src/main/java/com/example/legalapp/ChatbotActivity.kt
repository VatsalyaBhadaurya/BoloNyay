package com.example.legalapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.legalapp.adapters.ChatAdapter
import com.example.legalapp.models.ChatMessage

class ChatbotActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private lateinit var generativeModel: GenerativeModel

    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Initialize Gemini with configuration
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyAmSdliZN5lur_P-0i0E-bcNhV4MxGgqP4",
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )

        setupViews()
        setupChatbot()
        sendWelcomeMessage()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        adapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupChatbot() {
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            }
        }
    }

    private fun sendWelcomeMessage() {
        val welcomeMessage = "Hello! I'm NyayBot, your legal assistant. I can help you understand legal procedures, provide guidance on filing applications, and answer questions about Indian law. How can I assist you today?"
        chatMessages.add(ChatMessage(welcomeMessage, false))
        adapter.notifyItemInserted(chatMessages.size - 1)
    }

    private fun sendMessage(message: String) {
        chatMessages.add(ChatMessage(message, true))
        adapter.notifyItemInserted(chatMessages.size - 1)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prompt = """
                    Act as NyayBot, a knowledgeable and empathetic legal assistant specializing in Indian law. 
                    You should:
                    1. Provide clear, accurate information about legal procedures
                    2. Use simple language avoiding complex legal jargon
                    3. Be empathetic and understanding
                    4. Always suggest consulting a qualified lawyer for specific legal advice
                    5. Focus on Indian legal context
                    
                    User query: $message
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                
                withContext(Dispatchers.Main) {
                    chatMessages.add(ChatMessage(response.text ?: "I apologize, but I couldn't generate a response.", false))
                    adapter.notifyItemInserted(chatMessages.size - 1)
                    recyclerView.smoothScrollToPosition(chatMessages.size - 1)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    chatMessages.add(ChatMessage("I apologize, but I encountered an error: ${e.message}", false))
                    adapter.notifyItemInserted(chatMessages.size - 1)
                }
            }
        }
    }
} 