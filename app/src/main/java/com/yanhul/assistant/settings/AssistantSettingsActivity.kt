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
        fun field(hint: String, key: String, secret: Boolean = false): EditText = EditText(this).apply {
            this.hint = hint
            inputType = if (secret) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT
            setText(prefs.getString(key, ""))
        }
        val geminiKey = field("Gemini API key", "gemini_api_key", true)
        val geminiModel = field("Gemini model", "gemini_model")
        val compatibleBase = field("OpenAI-compatible base URL", "openai_base_url")
        val compatibleKey = field("OpenAI-compatible API key", "openai_api_key", true)
        val compatibleModel = field("OpenAI-compatible model", "openai_model")
        val save = Button(this).apply { text = "Save provider settings" }
        save.setOnClickListener {
            prefs.edit()
                .putString("gemini_api_key", geminiKey.text.toString().trim())
                .putString("gemini_model", geminiModel.text.toString().trim().ifEmpty { "gemini-2.5-flash" })
                .putString("openai_base_url", compatibleBase.text.toString().trim())
                .putString("openai_api_key", compatibleKey.text.toString().trim())
                .putString("openai_model", compatibleModel.text.toString().trim())
                .apply()
            Toast.makeText(this, "Provider settings saved on this device.", Toast.LENGTH_SHORT).show()
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            addView(TextView(this@AssistantSettingsActivity).apply {
                text = "AI Assistant\n\nConfigure multiple AI providers. Configured providers are tried in order and the next one is used when a request fails."
                textSize = 18f
            }, ViewGroup.LayoutParams(-1, -2))
            addView(geminiKey, ViewGroup.LayoutParams(-1, -2))
            addView(geminiModel, ViewGroup.LayoutParams(-1, -2))
            addView(compatibleBase, ViewGroup.LayoutParams(-1, -2))
            addView(compatibleKey, ViewGroup.LayoutParams(-1, -2))
            addView(compatibleModel, ViewGroup.LayoutParams(-1, -2))
            addView(save, ViewGroup.LayoutParams(-1, -2))
        })
    }
}
