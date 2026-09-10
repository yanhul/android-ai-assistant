package com.yanhul.assistant.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolRegistryTest {
    private data class Input(val value: String) : ToolInput
    private data class Output(val value: String) : ToolOutput

    private class EchoTool(
        id: String = "echo",
    ) : TypedTool<Input, Output> {
        override val definition = ToolDefinition(id, "Echoes typed input")
        override fun execute(input: Input): Output = Output(input.value)
    }

    @Test
    fun registers_and_executes_typed_tool() {
        val registry = ToolRegistry()
        registry.register(EchoTool())

        assertTrue(registry.contains("echo"))
        assertEquals(listOf("echo"), registry.ids())
        assertEquals(Output("hello"), registry.execute("echo", Input("hello")))
    }

    @Test
    fun preserves_registration_order_and_rejects_duplicate_ids() {
        val registry = ToolRegistry()
        registry.register(EchoTool("first"))
        registry.register(EchoTool("second"))

        assertEquals(listOf("first", "second"), registry.ids())
        expectIllegalArgument { registry.register(EchoTool("first")) }
    }

    @Test
    fun rejects_blank_definition_fields() {
        val registry = ToolRegistry()
        expectIllegalArgument { registry.register(EchoTool("   ")) }
        expectIllegalArgument {
            registry.register(object : TypedTool<Input, Output> {
                override val definition = ToolDefinition("invalid", "  ")
                override fun execute(input: Input): Output = Output(input.value)
            })
        }
        assertFalse(registry.contains("invalid"))
    }

    @Test
    fun unknown_tool_fails_closed() {
        val registry = ToolRegistry()

        expectIllegalState { registry.execute("missing", Input("hello")) }
    }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }

    private fun expectIllegalState(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
            // expected
        }
    }
}
