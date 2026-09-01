package com.yanhul.assistant.assistant

interface LLMProvider {
    val id: String
    val displayName: String
    fun isConfigured(): Boolean
    fun ask(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit)
}
