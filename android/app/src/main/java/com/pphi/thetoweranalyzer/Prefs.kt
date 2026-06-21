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

    // Centralized (AWS API Gateway)
    var centralizedEndpoint: String
        get() = prefs.getString("centralized_endpoint", "") ?: ""
        set(v) = prefs.edit().putString("centralized_endpoint", v).apply()

    var centralizedApiKey: String
        get() = prefs.getString("centralized_api_key", "") ?: ""
        set(v) = prefs.edit().putString("centralized_api_key", v).apply()

    // Legacy (make.com webhook)
    var legacyWebhookUrl: String
        get() = prefs.getString("legacy_webhook_url", "") ?: ""
        set(v) = prefs.edit().putString("legacy_webhook_url", v).apply()

    var legacyApiKey: String
        get() = prefs.getString("legacy_api_key", "") ?: ""
        set(v) = prefs.edit().putString("legacy_api_key", v).apply()
}
