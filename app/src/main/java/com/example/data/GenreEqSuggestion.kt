package com.example.data

import android.content.Context

/**
 * Suggests EQ preset based on current MediaSession title heuristics.
 * Falls back to Flat if unknown.
 * Batch 333
 */
object GenreEqSuggestion {
    enum class Preset { ROCK, POP, JAZZ, CLASSICAL, VOCAL, FLAT, BASS_BOOST }

    fun suggest(context: Context): Preset {
        val track = MediaNowPlaying.current(context) ?: return Preset.FLAT
        val title = track.title.lowercase()
        return when {
            title.contains("rock") || title.contains("metal") -> Preset.ROCK
            title.contains("pop") -> Preset.POP
            title.contains("jazz") -> Preset.JAZZ
            title.contains("classic") -> Preset.CLASSICAL
            title.contains("podcast") || title.contains("talk") -> Preset.VOCAL
            else -> Preset.FLAT
        }
    }

    fun presetName(p: Preset): String = when (p) {
        Preset.ROCK -> "Rock"
        Preset.POP -> "Pop"
        Preset.JAZZ -> "Jazz"
        Preset.CLASSICAL -> "Klassiek"
        Preset.VOCAL -> "Vocal"
        Preset.FLAT -> "Flat"
        Preset.BASS_BOOST -> "Bass+"
    }
}
