package com.yanhul.assistant.settings

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class AssistantSettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "AI Assistant\n\nSet this app as the system digital assistant in Android Settings.\n\nV0.1: VoiceInteractionService + command router scaffold."
            textSize = 18f
            setPadding(48, 48, 48, 48)
        })
    }
}
