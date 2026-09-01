package com.yanhul.assistant.assistant

import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

/** Minimal Gemini REST client. The API key is supplied at runtime and is never committed. */
class GeminiClient(private val apiKeyProvider: () -> String?) {
    private val main = Handler(Looper.getMainLooper())
    private val model = "gemini-2.5-flash"

    fun ask(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val key = apiKeyProvider()?.trim().orEmpty()
        if (key.isEmpty()) {
            onError("Gemini API key is not configured.")
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
                val body = JSONObject()
                    .put("contents", org.json.JSONArray().put(
                        JSONObject().put("role", "user").put(
                            "parts", org.json.JSONArray().put(JSONObject().put("text", prompt))
                        )
                    ))
                    .toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val response = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
                if (status !in 200..299) throw IllegalStateException("Gemini HTTP $status")

                val root = JSONObject(response)
                val text = root.optJSONArray("candidates")?.optJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)
                    ?.optString("text").orEmpty().trim()
                if (text.isEmpty()) throw IllegalStateException("Gemini returned no text")
                main.post { onResult(text) }
            } catch (t: Throwable) {
                main.post { onError("Gemini request failed: ${t.message ?: "unknown error"}") }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
