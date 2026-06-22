package com.pphi.thetoweranalyzer

/**
 * Bundled centralized ingest endpoints. The user picks the region nearest to
 * them; the underlying URL is never shown. Add a line here as each prod region
 * stack is deployed (see infra/bin/infra.ts).
 */
enum class Region(val displayName: String, val endpoint: String) {
    US_EAST(
        "US East (Ohio)",
        "https://u4qu1kwqt8.execute-api.us-east-2.amazonaws.com/prod/reports",
    );
    // Future regions (deploy stack, then uncomment):
    // EU_CENTRAL("Europe (Frankfurt)", "https://….execute-api.eu-central-1.amazonaws.com/prod/reports"),
    // AP_NORTHEAST("Asia Pacific (Tokyo)", "https://….execute-api.ap-northeast-1.amazonaws.com/prod/reports"),

    override fun toString() = displayName

    companion object {
        val ALL = entries.toList()
        val DEFAULT = US_EAST
    }
}
