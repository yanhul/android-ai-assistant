package com.yanhul.assistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.yanhul.assistant.settings.AssistantSettingsActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, AssistantSettingsActivity::class.java))
        finish()
    }
}
