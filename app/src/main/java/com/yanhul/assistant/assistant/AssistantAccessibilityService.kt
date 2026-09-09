package com.yanhul.assistant.assistant

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/** User-enabled Android UI bridge. No automation is possible until Android grants the service. */
class AssistantAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() { super.onServiceConnected(); instance = this }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit
    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }

    companion object {
        @Volatile private var instance: AssistantAccessibilityService? = null
        fun current(): AssistantAccessibilityService? = instance
    }
}
