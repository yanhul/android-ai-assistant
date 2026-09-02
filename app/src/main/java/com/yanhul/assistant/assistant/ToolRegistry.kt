package com.yanhul.assistant.assistant

/**
 * Deterministic registry for typed tools. Registration order is preserved for
 * stable inspection, while IDs are unique and required to be non-blank.
 */
class ToolRegistry {
    private val tools = linkedMapOf<String, RegisteredTool<*, *>>()

    fun <I : ToolInput, O : ToolOutput> register(tool: TypedTool<I, O>) {
        val id = tool.definition.id.trim()
        require(id.isNotEmpty()) { "Tool id must not be blank" }
        require(tool.definition.description.isNotBlank()) { "Tool description must not be blank" }
        require(id !in tools) { "Tool already registered: $id" }
        tools[id] = RegisteredTool(tool)
    }

    fun contains(id: String): Boolean = id in tools

    fun ids(): List<String> = tools.keys.toList()

    fun execute(id: String, input: ToolInput): ToolOutput {
        val registered = tools[id] ?: error("Unknown tool: $id")
        return registered.execute(input)
    }

    private class RegisteredTool<I : ToolInput, O : ToolOutput>(
        private val tool: TypedTool<I, O>,
    ) {
        fun execute(input: ToolInput): ToolOutput {
            @Suppress("UNCHECKED_CAST")
            return tool.execute(input as I)
        }
    }
}
