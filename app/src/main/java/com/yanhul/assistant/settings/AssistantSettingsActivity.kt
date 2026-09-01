package com.yanhul.assistant.settings

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class AssistantSettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("assistant", MODE_PRIVATE)
        val key = EditText(this).apply {
            hint = "Gemini API key"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setText(prefs.getString("gemini_api_key", ""))
        }
        val save = Button(this).apply { text = "Save Gemini key" }
        save.setOnClickListener {
            prefs.edit().putString("gemini_api_key", key.text.toString().trim()).apply()
            Toast.makeText(this, "Gemini key saved on this device.", Toast.LENGTH_SHORT).show()
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            addView(TextView(this@AssistantSettingsActivity).apply {
                text = "AI Assistant\n\nSet this app as the system digital assistant in Android Settings.\n\nGemini is used for requests that are not handled by explicit Android actions."
                textSize = 18f
            }, ViewGroup.LayoutParams(-1, -2))
            addView(key, ViewGroup.LayoutParams(-1, -2))
            addView(save, ViewGroup.LayoutParams(-1, -2))
        })
    }
}
