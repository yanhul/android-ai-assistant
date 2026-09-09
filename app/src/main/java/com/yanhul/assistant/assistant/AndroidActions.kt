package com.yanhul.assistant.assistant

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Explicit Android actions plus an optional, user-enabled UI automation boundary. */
class AndroidActions(private val context: Context) {
    private val automation = AndroidAutomation { AssistantAccessibilityService.current() }

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

    fun back(): CommandResult = if (automation.back()) CommandResult("Going back.") else CommandResult("UI control is not enabled.")
    fun home(): CommandResult = if (automation.home()) CommandResult("Going home.") else CommandResult("UI control is not enabled.")
    fun click(text: String): CommandResult = if (automation.clickText(text)) CommandResult("Done.") else CommandResult("I couldn't click '$text'. UI control may be disabled or the text may not be visible.")
    fun tap(x: Float, y: Float): CommandResult = if (automation.tap(x, y)) CommandResult("Done.") else CommandResult("I couldn't tap there. UI control may be disabled.")
    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float): CommandResult = if (automation.swipe(x1, y1, x2, y2)) CommandResult("Done.") else CommandResult("I couldn't swipe. UI control may be disabled.")
}
