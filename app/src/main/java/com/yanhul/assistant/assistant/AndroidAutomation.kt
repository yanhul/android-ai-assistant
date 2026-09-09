package com.yanhul.assistant.assistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityNodeInfo

/** Optional UI automation boundary. It is inert until the user enables accessibility. */
class AndroidAutomation(private val serviceProvider: () -> AccessibilityService?) {
    fun enabled(): Boolean = serviceProvider() != null

    fun back(): Boolean = serviceProvider()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) == true
    fun home(): Boolean = serviceProvider()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME) == true

    fun clickText(text: String): Boolean {
        val root = serviceProvider()?.rootInActiveWindow ?: return false
        val node = findText(root, text) ?: return false
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    fun tap(x: Float, y: Float, durationMs: Long = 50L): Boolean {
        val service = serviceProvider() ?: return false
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(1)))
            .build()
        return service.dispatchGesture(gesture, null, null)
    }

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 400L): Boolean {
        val service = serviceProvider() ?: return false
        val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(1)))
            .build()
        return service.dispatchGesture(gesture, null, null)
    }

    private fun findText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val matches = node.findAccessibilityNodeInfosByText(text)
        if (matches.isNotEmpty()) return matches.first()
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findText(child, text)
            if (found != null) return found
        }
        return null
    }
}
