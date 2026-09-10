package com.yanhul.assistant.assistant

import org.junit.Assert.assertFalse
import org.junit.Test

class AndroidAutomationTest {
    @Test fun disabledBoundaryFailsClosed() {
        val automation = AndroidAutomation { null }
        assertFalse(automation.enabled())
        assertFalse(automation.back())
        assertFalse(automation.home())
        assertFalse(automation.clickText("Settings"))
        assertFalse(automation.tap(10f, 10f))
        assertFalse(automation.swipe(1f, 1f, 2f, 2f))
    }
}
