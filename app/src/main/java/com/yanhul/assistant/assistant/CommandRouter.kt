package com.yanhul.assistant.assistant

import android.content.Context

data class CommandResult(val message: String, val closeSession: Boolean = false)

class CommandRouter {
    fun handle(context: Context, text: String): CommandResult {
        val normalized = text.trim().lowercase()
        return when {
            normalized == "assistant_ready" -> CommandResult("Ready.")
            normalized.contains("hello") || normalized.contains("xin chào") ->
                CommandResult("Hello. I'm ready.")
            normalized.contains("stop") || normalized.contains("dừng") ->
                CommandResult("Okay.", closeSession = true)
            else -> CommandResult("I heard: $text")
        }
    }
}
