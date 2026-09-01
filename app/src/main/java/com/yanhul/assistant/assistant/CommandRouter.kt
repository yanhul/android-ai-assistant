package com.yanhul.assistant.assistant

import android.content.Context

data class CommandResult(val message: String, val closeSession: Boolean = false)

class CommandRouter {
    private lateinit var actions: AndroidActions
    private var gemini: GeminiClient? = null

    fun handle(context: Context, text: String, onResult: (CommandResult) -> Unit) {
        if (!::actions.isInitialized) actions = AndroidActions(context)
        if (gemini == null) {
            val prefs = context.getSharedPreferences("assistant", Context.MODE_PRIVATE)
            gemini = GeminiClient { prefs.getString("gemini_api_key", null) }
        }

        val normalized = text.trim().lowercase()
        val deterministic = when {
            normalized == "assistant_ready" -> CommandResult("Ready.")
            normalized.contains("hello") || normalized.contains("xin chào") -> CommandResult("Hello. I'm ready.")
            normalized.contains("stop") || normalized.contains("dừng") -> CommandResult("Okay.", closeSession = true)
            normalized == "mở cài đặt" || normalized == "open settings" || normalized == "mở settings" -> actions.openSettings()
            normalized.startsWith("mở ") && normalized.contains("http") -> actions.openUrl(normalized.substringAfter("mở ").trim())
            normalized.startsWith("open ") && normalized.contains("http") -> actions.openUrl(normalized.substringAfter("open ").trim())
            else -> null
        }

        if (deterministic != null) {
            onResult(deterministic)
            return
        }

        gemini?.ask(
            prompt = "You are the Android personal assistant. Answer the user's request concisely and naturally. Do not claim to have performed an Android action unless this app explicitly performed it. User: $text",
            onResult = { reply -> onResult(CommandResult(reply)) },
            onError = { error -> onResult(CommandResult("I heard: $text. $error")) },
        )
    }
}
