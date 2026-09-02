package com.yanhul.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.yanhul.assistant.assistant.CommandRouter
import com.yanhul.assistant.assistant.VoiceIO
import com.yanhul.assistant.settings.AssistantSettingsActivity

class MainActivity : ComponentActivity() {
    private val router = CommandRouter()
    private lateinit var voice: VoiceIO
    private lateinit var status: TextView

    private val microphonePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            status.text = "Listening…"
            voice.startListening()
        } else {
            status.text = "Voice error: Microphone permission is required."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = TextView(this).apply {
            text = "AI Assistant\n\nTap Listen and speak."
            textSize = 20f
            setPadding(32, 48, 32, 24)
        }
        voice = VoiceIO(
            context = this,
            onText = ::handleSpeech,
            onError = { message -> status.text = "Voice error: $message" },
        )

        val listen = Button(this).apply {
            text = "Listen"
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
                    return@setOnClickListener
                }
                status.text = "Listening…"
                voice.startListening()
            }
        }
        val settings = Button(this).apply {
            text = "Settings"
            setOnClickListener { startActivity(Intent(this@MainActivity, AssistantSettingsActivity::class.java)) }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            addView(status, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(listen, LinearLayout.LayoutParams(-1, -2))
            addView(settings, LinearLayout.LayoutParams(-1, -2))
        })
    }

    private fun handleSpeech(text: String) {
        status.text = "You: $text\n\nThinking…"
        router.handle(this, text) { result ->
            status.text = "You: $text\n\nAssistant: ${result.message}"
            voice.speak(result.message)
        }
    }

    override fun onDestroy() {
        voice.release()
        super.onDestroy()
    }
}
