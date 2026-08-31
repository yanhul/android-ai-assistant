package com.yanhul.assistant.assistant

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Explicit, user-visible Android actions. No privileged automation is assumed. */
class AndroidActions(private val context: Context) {
    fun openUrl(url: String): CommandResult {
        val uri = runCatching { Uri.parse(url) }.getOrNull()
            ?: return CommandResult("I couldn't open that link.")
        return runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            CommandResult("Opening it.")
        }.getOrElse { CommandResult("I couldn't open that link.") }
    }

    fun openSettings(): CommandResult = runCatching {
        context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        CommandResult("Opening settings.")
    }.getOrElse { CommandResult("I couldn't open settings.") }
}
