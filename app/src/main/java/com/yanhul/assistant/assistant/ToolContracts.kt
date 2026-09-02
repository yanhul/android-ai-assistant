package com.yanhul.assistant.assistant

/** Marker for a tool's strongly typed input value. */
interface ToolInput

/** Marker for a tool's strongly typed output value. */
interface ToolOutput

data class ToolDefinition(
    val id: String,
    val description: String,
)

/** A tool keeps its input/output types explicit at the implementation boundary. */
interface TypedTool<I : ToolInput, O : ToolOutput> {
    val definition: ToolDefinition
    fun execute(input: I): O
}
