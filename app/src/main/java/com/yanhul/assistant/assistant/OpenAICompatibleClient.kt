package com.yanhul.assistant.assistant

import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class OpenAICompatibleClient(
    override val id: String,
    override val displayName: String,
    private val baseUrlProvider: () -> String?,
    private val modelProvider: () -> String?,
    private val apiKeyProvider: () -> String?,
) : LLMProvider {
    private val main = Handler(Looper.getMainLooper())

    override fun isConfigured(): Boolean =
        !apiKeyProvider().isNullOrBlank() && !baseUrlProvider().isNullOrBlank() && !modelProvider().isNullOrBlank()

    override fun ask(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val key = apiKeyProvider()?.trim().orEmpty()
        val base = baseUrlProvider()?.trim()?.trimEnd('/').orEmpty()
        val model = modelProvider()?.trim().orEmpty()
        if (key.isEmpty() || base.isEmpty() || model.isEmpty()) {
            onError("$displayName is not configured.")
            return
        }
        Thread {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL("$base/chat/completions").openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15_000
                    readTimeout = 30_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer $key")
                }
                val messages = JSONArray().put(
                    JSONObject().put("role", "user").put("content", prompt)
                )
                val body = JSONObject().put("model", model).put("messages", messages).toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val response = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
                if (status !in 200..299) throw IllegalStateException("$displayName HTTP $status")
                val text = JSONObject(response).optJSONArray("choices")?.optJSONObject(0)
                    ?.optJSONObject("message")?.optString("content").orEmpty().trim()
                if (text.isEmpty()) throw IllegalStateException("$displayName returned no text")
                main.post { onResult(text) }
            } catch (t: Throwable) {
                main.post { onError("$displayName request failed: ${t.message ?: "unknown error"}") }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
