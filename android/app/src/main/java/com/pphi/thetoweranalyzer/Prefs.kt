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

    // Centralized (AWS API Gateway) — user picks a region; the URL is bundled, never shown.
    var region: Region
        get() = runCatching { Region.valueOf(prefs.getString("region", Region.DEFAULT.name)!!) }
            .getOrDefault(Region.DEFAULT)
        set(v) = prefs.edit().putString("region", v.name).apply()

    /** Derived ingest endpoint for the selected region. */
    val centralizedEndpoint: String
        get() = region.endpoint

    // Legacy (make.com webhook)
    var legacyWebhookUrl: String
        get() = prefs.getString("legacy_webhook_url", "") ?: ""
        set(v) = prefs.edit().putString("legacy_webhook_url", v).apply()
}
