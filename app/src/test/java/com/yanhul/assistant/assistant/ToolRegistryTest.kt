package com.yanhul.assistant.assistant

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolRegistryTest {
    private data class EchoInput(val text: String) : ToolInput
    private data class EchoOutput(val text: String) : ToolOutput

    private class EchoTool : TypedTool<EchoInput, EchoOutput> {
        override val definition = ToolDefinition("echo", "Echo text")
        override fun execute(input: EchoInput): EchoOutput = EchoOutput(input.text)
    }

    @Test
    fun registers_and_executes_typed_tool() {
        val registry = ToolRegistry()
        registry.register(EchoTool())

        assertTrue(registry.contains("echo"))
        assertEquals(listOf("echo"), registry.ids())
        assertEquals(EchoOutput("hello"), registry.execute("echo", EchoInput("hello")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejects_duplicate_tool_ids() {
        val registry = ToolRegistry()
        registry.register(EchoTool())
        registry.register(EchoTool())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejects_blank_tool_ids() {
        val registry = ToolRegistry()
        registry.register(object : TypedTool<EchoInput, EchoOutput> {
            override val definition = ToolDefinition("  ", "invalid")
            override fun execute(input: EchoInput) = EchoOutput(input.text)
        })
    }
}
