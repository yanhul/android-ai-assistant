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
            normalized.startsWith("mở ") && normalized.contains("http") -> actions.openUrl(normalized.substringAfter("mở ").trim())
            normalized.startsWith("open ") && normalized.contains("http") -> actions.openUrl(normalized.substringAfter("open ").trim())
            else -> null
        }
        if (deterministic != null) {
            onResult(deterministic)
            return
        }

        val providers = ProviderRegistry(context).providers().filter { it.isConfigured() }
        if (providers.isEmpty()) {
            onResult(CommandResult("I heard: $text. No AI provider is configured."))
            return
        }

        askWithFallback(providers, 0, text, onResult)
    }

    private fun askWithFallback(
        providers: List<LLMProvider>,
        index: Int,
        text: String,
        onResult: (CommandResult) -> Unit,
    ) {
        if (index >= providers.size) {
            onResult(CommandResult("I heard: $text. All configured AI providers failed."))
            return
        }
        val provider = providers[index]
        provider.ask(
            prompt = "You are the Android personal assistant. Answer the user's request concisely and naturally. Do not claim to have performed an Android action unless this app explicitly performed it. User: $text",
            onResult = { reply -> onResult(CommandResult(reply)) },
            onError = { askWithFallback(providers, index + 1, text, onResult) },
        )
    }
}
