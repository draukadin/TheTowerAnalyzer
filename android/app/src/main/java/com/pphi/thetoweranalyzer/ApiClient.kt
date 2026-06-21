package com.pphi.thetoweranalyzer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

sealed class SubmitResult {
    data object Success : SubmitResult()
    data class Failure(val statusCode: Int, val message: String) : SubmitResult()
    data class NetworkError(val message: String) : SubmitResult()
}

object ApiClient {

    suspend fun submitCentralized(
        endpoint: String,
        apiKey: String,
        playerId: String,
        runType: RunType,
        dissonanceType: DissonanceType?,
        body: String,
    ): SubmitResult = withContext(Dispatchers.IO) {
        val url = URL(buildUrl(endpoint, runType, dissonanceType))
        post(url, body) { conn ->
            conn.setRequestProperty("X-Api-Key", apiKey)
            conn.setRequestProperty("X-Player-Id", playerId)
        }
    }

    suspend fun submitLegacy(
        webhookUrl: String,
        apiKey: String,
        runType: RunType,
        dissonanceType: DissonanceType?,
        body: String,
    ): SubmitResult = withContext(Dispatchers.IO) {
        val url = URL(buildUrl(webhookUrl, runType, dissonanceType))
        post(url, body) { conn ->
            conn.setRequestProperty("X-Api-Key", apiKey)
        }
    }

    private fun buildUrl(base: String, runType: RunType, dissonanceType: DissonanceType?): String {
        val sb = StringBuilder("$base?runType=${runType.displayName}")
        if (dissonanceType != null) sb.append("&dissonanceType=${dissonanceType.displayName}")
        return sb.toString()
    }

    private fun post(
        url: URL,
        body: String,
        headers: (HttpURLConnection) -> Unit,
    ): SubmitResult {
        return try {
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            headers(conn)

            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

            val code = conn.responseCode
            if (code in 200..299) {
                SubmitResult.Success
            } else {
                val msg = runCatching { conn.errorStream?.bufferedReader()?.readText() }.getOrNull() ?: ""
                SubmitResult.Failure(code, msg)
            }
        } catch (e: IOException) {
            SubmitResult.NetworkError(e.message ?: "Network error")
        }
    }
}
