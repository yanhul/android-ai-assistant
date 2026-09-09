package com.yanhul.assistant.assistant

import android.content.Context

data class CommandResult(val message: String, val closeSession: Boolean = false)

class CommandRouter {
    private lateinit var actions: AndroidActions

    fun handle(context: Context, text: String, onResult: (CommandResult) -> Unit) {
        if (!::actions.isInitialized) actions = AndroidActions(context)
        val normalized = text.trim().lowercase()
        val deterministic = when {
            normalized == "assistant_ready" -> CommandResult("Ready.")
            normalized.contains("hello") || normalized.contains("xin chào") -> CommandResult("Hello. I'm ready.")
            normalized.contains("stop") || normalized.contains("dừng") -> CommandResult("Okay.", closeSession = true)
            normalized == "mở cài đặt" || normalized == "open settings" || normalized == "mở settings" -> actions.openSettings()
            normalized == "back" || normalized == "quay lại" -> actions.back()
            normalized == "home" || normalized == "về màn hình chính" -> actions.home()
            normalized.startsWith("click ") -> actions.click(text.trim().substring(6).trim())
            normalized.startsWith("bấm ") -> actions.click(text.trim().substring(4).trim())
            normalized.startsWith("mở ") && normalized.contains("http") -> actions.openUrl(text.trim().substringAfter("mở ").trim())
            normalized.startsWith("open ") && normalized.contains("http") -> actions.openUrl(text.trim().substringAfter("open ").trim())
            else -> null
        }
        if (deterministic != null) { onResult(deterministic); return }

        val providers = ProviderRegistry(context).providers().filter { it.isConfigured() }
        if (providers.isEmpty()) {
            onResult(CommandResult("I heard: $text. No AI provider is configured.")); return
        }
        askWithFallback(providers, 0, text, onResult)
    }

    private fun askWithFallback(providers: List<LLMProvider>, index: Int, text: String, onResult: (CommandResult) -> Unit) {
        if (index >= providers.size) { onResult(CommandResult("I heard: $text. All configured AI providers failed.")); return }
        providers[index].ask(
            prompt = "You are the Android personal assistant. Answer concisely. Do not claim an Android action was performed unless this app explicitly performed it. User: $text",
            onResult = { reply -> onResult(CommandResult(reply)) },
            onError = { askWithFallback(providers, index + 1, text, onResult) },
        )
    }
}
