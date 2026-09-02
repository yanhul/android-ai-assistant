package com.yanhul.assistant.assistant

/** Deterministic provider-selection policy kept independent from Android framework state. */
object ProviderSelector {
    fun firstConfigured(providers: List<LLMProvider>): LLMProvider? =
        providers.firstOrNull { it.isConfigured() }
}
