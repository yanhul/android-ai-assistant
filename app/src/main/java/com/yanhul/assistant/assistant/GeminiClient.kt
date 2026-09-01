package com.yanhul.assistant.assistant

import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

/** Gemini REST provider. Key and model are supplied at runtime. */
class GeminiClient(
    private val apiKeyProvider: () -> String?,
    private val modelProvider: () -> String?,
) : LLMProvider {
    override val id = "gemini"
    override val displayName = "Google Gemini"
    private val main = Handler(Looper.getMainLooper())

    override fun isConfigured(): Boolean =
        !apiKeyProvider().isNullOrBlank() && !modelProvider().isNullOrBlank()

    override fun ask(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val key = apiKeyProvider()?.trim().orEmpty()
        val model = modelProvider()?.trim().orEmpty()
        if (key.isEmpty() || model.isEmpty()) {
            onError("Google Gemini is not configured.")
            return
        }
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent")
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15_000
                    readTimeout = 30_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-goog-api-key", key)
                }
                val body = JSONObject().put("contents", JSONArray().put(
                    JSONObject().put("role", "user").put(
                        "parts", JSONArray().put(JSONObject().put("text", prompt))
                    )
                )).toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val response = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
                if (status !in 200..299) throw IllegalStateException("Google Gemini HTTP $status")
                val text = JSONObject(response).optJSONArray("candidates")?.optJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)
                    ?.optString("text").orEmpty().trim()
                if (text.isEmpty()) throw IllegalStateException("Google Gemini returned no text")
                main.post { onResult(text) }
            } catch (t: Throwable) {
                main.post { onError("Google Gemini request failed: ${t.message ?: "unknown error"}") }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
