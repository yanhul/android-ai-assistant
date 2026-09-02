package com.yanhul.assistant.assistant

import android.content.Context

class ProviderRegistry(private val context: Context) {
    private val prefs = context.getSharedPreferences("assistant", Context.MODE_PRIVATE)

    fun providers(): List<LLMProvider> = listOf(
        GeminiClient(
            apiKeyProvider = { prefs.getString("gemini_api_key", null) },
            modelProvider = { prefs.getString("gemini_model", "gemini-2.5-flash") },
        ),
        OpenAICompatibleClient(
            id = "openai-compatible",
            displayName = "OpenAI-compatible provider",
            baseUrlProvider = { prefs.getString("openai_base_url", null) },
            modelProvider = { prefs.getString("openai_model", null) },
            apiKeyProvider = { prefs.getString("openai_api_key", null) },
        ),
    )

    fun firstConfigured(): LLMProvider? = ProviderSelector.firstConfigured(providers())
}
