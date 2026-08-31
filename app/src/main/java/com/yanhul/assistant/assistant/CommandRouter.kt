package com.yanhul.assistant.assistant

import android.content.Context

data class CommandResult(val message: String, val closeSession: Boolean = false)

class CommandRouter {
    private lateinit var actions: AndroidActions

    fun handle(context: Context, text: String): CommandResult {
        if (!::actions.isInitialized) actions = AndroidActions(context)
        val normalized = text.trim().lowercase()
        return when {
            normalized == "assistant_ready" -> CommandResult("Ready.")
            normalized.contains("hello") || normalized.contains("xin chào") ->
                CommandResult("Hello. I'm ready.")
            normalized.contains("stop") || normalized.contains("dừng") ->
                CommandResult("Okay.", closeSession = true)
            normalized == "mở cài đặt" || normalized == "open settings" || normalized == "mở settings" ->
                actions.openSettings()
            normalized.startsWith("mở ") && normalized.contains("http") ->
                actions.openUrl(normalized.substringAfter("mở ").trim())
            normalized.startsWith("open ") && normalized.contains("http") ->
                actions.openUrl(normalized.substringAfter("open ").trim())
            else -> CommandResult("I heard: $text")
        }
    }
}
