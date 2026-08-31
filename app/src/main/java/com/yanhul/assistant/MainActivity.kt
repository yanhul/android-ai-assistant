package com.yanhul.assistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, Class.forName("com.yanhul.assistant.settings.AssistantSettingsActivity")))
        finish()
    }
}
