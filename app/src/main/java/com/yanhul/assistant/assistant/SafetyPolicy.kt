package com.yanhul.assistant.assistant

/**
 * Classifies explicit assistant actions before execution.
 * This is an implementation guard; it does not grant authority or bypass AIOS controls.
 */
class SafetyPolicy {
    fun decision(action: String, argument: String? = null): SafetyDecision {
        val normalizedAction = action.trim().lowercase()
        val normalizedArgument = argument?.trim()?.lowercase().orEmpty()

        if (normalizedAction.isBlank()) return SafetyDecision.Block("Action is missing.")

        if (normalizedAction in destructiveActions || destructiveWords.any { normalizedArgument.contains(it) }) {
            return SafetyDecision.Confirm("Confirmation required before a potentially destructive action.")
        }

        if (normalizedAction in ambiguousActions || normalizedArgument.isBlank() && normalizedAction in argumentRequiredActions) {
            return SafetyDecision.Confirm("Confirmation or a more specific target is required.")
        }

        return SafetyDecision.Allow
    }

    private companion object {
        val destructiveActions = setOf(
            "delete", "remove", "uninstall", "erase", "factory_reset", "clear_data", "send_payment"
        )
        val ambiguousActions = setOf("open", "launch", "send", "share", "submit")
        val argumentRequiredActions = setOf("click", "tap", "swipe", "open_url")
        val destructiveWords = setOf("delete", "uninstall", "factory reset", "clear all data", "send payment")
    }
}

sealed interface SafetyDecision {
    data object Allow : SafetyDecision
    data class Confirm(val reason: String) : SafetyDecision
    data class Block(val reason: String) : SafetyDecision
}
