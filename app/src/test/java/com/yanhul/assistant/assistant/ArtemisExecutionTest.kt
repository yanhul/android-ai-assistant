package com.yanhul.assistant.assistant

import org.junit.Assert.assertEquals
import org.junit.Test

class ArtemisExecutionTest {
    @Test
    fun unavailableProviderReturnsUnknown() {
        val provider = ArtemisExecutionProvider { false }

        val receipt = provider.execute(
            AndroidExecutionRequest(task = "Open Settings"),
        )

        assertEquals(AndroidExecutionStatus.UNKNOWN, receipt.status)
        assertEquals("artemis", receipt.providerId)
    }

    @Test
    fun blankTaskIsBlocked() {
        val provider = ArtemisExecutionProvider { true }

        val receipt = provider.execute(
            AndroidExecutionRequest(task = "  "),
        )

        assertEquals(AndroidExecutionStatus.BLOCKED, receipt.status)
    }

    @Test
    fun availableProviderStillDoesNotClaimExecutionWithoutTransport() {
        val provider = ArtemisExecutionProvider { true }

        val receipt = provider.execute(
            AndroidExecutionRequest(
                task = "Open Settings",
                profile = ArtemisProfile.PRO,
            ),
        )

        assertEquals(AndroidExecutionStatus.UNKNOWN, receipt.status)
        assertEquals("ARTEMIS transport is not attached to this APK", receipt.error)
    }
}
