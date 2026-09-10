package com.yanhul.assistant.assistant

/**
 * External Android execution capability backed by a host-side ARTEMIS/MCP
 * installation. This contract deliberately does not implement transport:
 * MCP belongs to the host/provider boundary, while this APK remains the
 * user-facing assistant and action admission boundary.
 */
interface AndroidExecutionProvider {
    val id: String
    fun isAvailable(): Boolean
    fun execute(request: AndroidExecutionRequest): AndroidExecutionReceipt
}

data class AndroidExecutionRequest(
    val task: String,
    val deviceSerial: String? = null,
    val profile: ArtemisProfile = ArtemisProfile.FLASH,
    val requiresConfirmation: Boolean = true,
)

enum class ArtemisProfile {
    FLASH,
    PRO,
}

enum class AndroidExecutionStatus {
    ACCEPTED,
    SUCCEEDED,
    FAILED,
    UNKNOWN,
    BLOCKED,
}

data class AndroidExecutionReceipt(
    val providerId: String,
    val status: AndroidExecutionStatus,
    val traceId: String? = null,
    val observedResult: String? = null,
    val error: String? = null,
)

/**
 * Configuration-only ARTEMIS provider descriptor.
 *
 * Runtime MCP transport is intentionally external to the APK. A host-side
 * adapter can implement AndroidExecutionProvider and map ARTEMIS MCP receipts
 * into this type without weakening the application's authority boundary.
 */
class ArtemisExecutionProvider(
    private val availability: () -> Boolean,
) : AndroidExecutionProvider {
    override val id: String = "artemis"

    override fun isAvailable(): Boolean = availability()

    override fun execute(request: AndroidExecutionRequest): AndroidExecutionReceipt {
        if (request.task.isBlank()) {
            return AndroidExecutionReceipt(
                providerId = id,
                status = AndroidExecutionStatus.BLOCKED,
                error = "Task must not be blank",
            )
        }

        if (!isAvailable()) {
            return AndroidExecutionReceipt(
                providerId = id,
                status = AndroidExecutionStatus.UNKNOWN,
                error = "ARTEMIS provider is unavailable",
            )
        }

        // Deliberately fail closed until a real host transport is attached.
        return AndroidExecutionReceipt(
            providerId = id,
            status = AndroidExecutionStatus.UNKNOWN,
            error = "ARTEMIS transport is not attached to this APK",
        )
    }
}
