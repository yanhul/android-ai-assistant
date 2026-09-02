package com.yanhul.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yanhul.assistant.assistant.CommandRouter
import com.yanhul.assistant.assistant.VoiceIO
import com.yanhul.assistant.settings.AssistantSettingsActivity

class MainActivity : ComponentActivity() {
    private val router = CommandRouter()
    private lateinit var voice: VoiceIO
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        voice = VoiceIO(this)
        status = TextView(this).apply {
            text = "AI Assistant\n\nTap Listen and speak."
            textSize = 20f
            setPadding(32, 48, 32, 24)
        }
        val listen = Button(this).apply { text = "Listen" }
        val settings = Button(this).apply { text = "Settings" }
        listen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
                return@setOnClickListener
            }
            status.text = "Listening…"
            voice.listen(
                onText = { text ->
                    status.text = "You: $text\n\nThinking…"
                    router.handle(this, text) { result ->
                        status.text = "You: $text\n\nAssistant: ${result.message}"
                        if (result.closeSession) voice.stop()
                    }
                },
                onError = { message -> status.text = "Voice error: $message" },
            )
        }
        settings.setOnClickListener { startActivity(Intent(this, AssistantSettingsActivity::class.java)) }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            addView(status, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(listen, LinearLayout.LayoutParams(-1, -2))
            addView(settings, LinearLayout.LayoutParams(-1, -2))
        })
    }

    override fun onDestroy() {
        voice.stop()
        super.onDestroy()
    }
}
