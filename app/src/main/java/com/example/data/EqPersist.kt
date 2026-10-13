package com.example.data

import android.content.Context
import com.example.dsp.EqPreset

/**
 * Opslaan/laden custom 10-band EQ curves via SharedPreferences.
 * Call save/load from EQ UI of bij connect.
 */
object EqPersist {
    private const val PREFS = "soundmax_eq_persist"
    private const val KEY_BANDS = "custom_bands"
    private const val KEY_NAME = "custom_name"
    private const val KEY_BASS = "custom_bass"
    private const val KEY_VIRT = "custom_virt"
    private const val KEY_LOUD = "custom_loud"
    private const val KEY_CLARITY = "custom_clarity"

    fun save(context: Context, preset: EqPreset) {
        val bands = preset.bandGains.joinToString(",")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_BANDS, bands)
            .putString(KEY_NAME, preset.name)
            .putInt(KEY_BASS, preset.bassBoost)
            .putInt(KEY_VIRT, preset.virtualizer)
            .putInt(KEY_LOUD, preset.loudness)
            .putFloat(KEY_CLARITY, preset.clarity)
            .apply()
    }

    fun load(context: Context): EqPreset? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val bandsStr = prefs.getString(KEY_BANDS, null) ?: return null
        val bands = bandsStr.split(",").mapNotNull { it.toFloatOrNull() }
        if (bands.size != 10) return null
        return EqPreset(
            name = prefs.getString(KEY_NAME, "Custom") ?: "Custom",
            bandGains = bands,
            bassBoost = prefs.getInt(KEY_BASS, 0),
            virtualizer = prefs.getInt(KEY_VIRT, 0),
            loudness = prefs.getInt(KEY_LOUD, 0),
            clarity = prefs.getFloat(KEY_CLARITY, 0f),
            isCustom = true,
            category = "Persist",
            description = "Opgeslagen custom curve"
        )
    }

    fun hasSaved(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).contains(KEY_BANDS)
}
