package com.yanhul.assistant.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProviderSelectorTest {
    private class FakeProvider(
        override val id: String,
        private val configured: Boolean,
    ) : LLMProvider {
        override val displayName: String = id
        override fun isConfigured(): Boolean = configured
        override fun ask(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) = Unit
    }

    @Test
    fun selects_first_configured_provider_in_declared_order() {
        val first = FakeProvider("first", false)
        val second = FakeProvider("second", true)
        val third = FakeProvider("third", true)

        assertEquals(second, ProviderSelector.firstConfigured(listOf(first, second, third)))
    }

    @Test
    fun returns_null_when_no_provider_is_configured() {
        val providers = listOf(
            FakeProvider("first", false),
            FakeProvider("second", false),
        )

        assertNull(ProviderSelector.firstConfigured(providers))
    }
}
