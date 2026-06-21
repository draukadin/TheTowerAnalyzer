package com.pphi.thetoweranalyzer

enum class RunType(val displayName: String) {
    FARMING("Farming"),
    MILESTONE("Milestone"),
    TOURNAMENT("Tournament"),
    EVENT("Event"),
    DISSONANCE("Dissonance");

    val requiresDissonanceType get() = this == DISSONANCE

    override fun toString() = displayName

    companion object {
        val ALL = entries.toList()
    }
}

enum class DissonanceType(val displayName: String) {
    ATTACK("Attack"),
    DEFENSE("Defense"),
    UTILITY("Utility"),
    ULTIMATE_WEAPON("Ultimate Weapon");

    override fun toString() = displayName

    companion object {
        val ALL = entries.toList()
    }
}
