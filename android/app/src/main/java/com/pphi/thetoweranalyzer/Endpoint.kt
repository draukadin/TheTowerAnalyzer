package com.pphi.thetoweranalyzer

/**
 * A selectable centralized ingest endpoint shown in Settings.
 *
 * Prod regions come from [Region]. A "Dev" choice is prepended only when
 * [BuildConfig.DEV_ENDPOINT] is non-empty, which happens solely in the debug
 * build type (see app/build.gradle.kts). The release APK ships with an empty
 * DEV_ENDPOINT, so the dev option — and its URL — are physically absent from it.
 *
 * Selection is persisted by [key] (stable across reinstalls), not by list index.
 */
data class Endpoint(val key: String, val displayName: String, val url: String) {

    override fun toString() = displayName

    companion object {
        val ALL: List<Endpoint> = buildList {
            if (BuildConfig.DEV_ENDPOINT.isNotEmpty()) {
                add(Endpoint("DEV", "Dev (local)", BuildConfig.DEV_ENDPOINT))
            }
            Region.ALL.forEach { add(Endpoint(it.name, it.displayName, it.endpoint)) }
        }

        val DEFAULT: Endpoint = ALL.first { it.key == Region.DEFAULT.name }

        fun byKey(key: String): Endpoint = ALL.firstOrNull { it.key == key } ?: DEFAULT
    }
}
