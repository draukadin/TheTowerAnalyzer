package com.pphi.thetoweranalyzer

import android.content.Context

enum class SubmitMode { CENTRALIZED, LEGACY }

class Prefs(context: Context) {

    private val prefs = context.getSharedPreferences("tower_analyzer", Context.MODE_PRIVATE)

    var mode: SubmitMode
        get() = SubmitMode.valueOf(prefs.getString("mode", SubmitMode.CENTRALIZED.name)!!)
        set(v) = prefs.edit().putString("mode", v.name).apply()

    var playerId: String
        get() = prefs.getString("player_id", "") ?: ""
        set(v) = prefs.edit().putString("player_id", v).apply()

    // Centralized (AWS API Gateway) — user picks an endpoint; the URL is bundled, never shown.
    var endpoint: Endpoint
        get() = Endpoint.byKey(prefs.getString("endpoint_key", Endpoint.DEFAULT.key)!!)
        set(v) = prefs.edit().putString("endpoint_key", v.key).apply()

    /** Derived ingest URL for the selected endpoint. */
    val centralizedEndpoint: String
        get() = endpoint.url

    // Legacy (make.com webhook)
    var legacyWebhookUrl: String
        get() = prefs.getString("legacy_webhook_url", "") ?: ""
        set(v) = prefs.edit().putString("legacy_webhook_url", v).apply()
}
