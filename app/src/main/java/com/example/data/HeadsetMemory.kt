package com.example.data

import android.content.Context
import com.example.dsp.EqPreset

/** Onthoudt de laatste EQ per Bluetooth-headsetnaam. */
class HeadsetMemory(context: Context) {
    private val prefs = context.getSharedPreferences("soundmax_headset_memory", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) { prefs.edit().putBoolean(KEY_ENABLED, value).apply() }

    fun save(headsetName: String?, preset: EqPreset, bands: List<Float>) {
        if (!enabled) return
        val key = normalize(headsetName) ?: return
        prefs.edit()
            .putString("$key.name", preset.name)
            .putString("$key.bands", bands.joinToString(","))
            .putInt("$key.bass", preset.bassBoost)
            .putInt("$key.virt", preset.virtualizer)
            .putInt("$key.loud", preset.loudness)
            .putFloat("$key.clarity", preset.clarity)
            .putLong("$key.ts", System.currentTimeMillis())
            .apply()
    }

    fun load(headsetName: String?): EqPreset? {
        if (!enabled) return null
        val key = normalize(headsetName) ?: return null
        val name = prefs.getString("$key.name", null) ?: return null
        val bands = prefs.getString("$key.bands", null)
            ?.split(",")
            ?.mapNotNull { it.toFloatOrNull() }
            ?: return null
        if (bands.size != 10) return null
        return EqPreset(
            name = name,
            bandGains = bands,
            bassBoost = prefs.getInt("$key.bass", 0),
            virtualizer = prefs.getInt("$key.virt", 0),
            loudness = prefs.getInt("$key.loud", 0),
            clarity = prefs.getFloat("$key.clarity", 0f),
            isCustom = true,
            category = "Headset",
            description = "Laatst gebruikt op $headsetName"
        )
    }

    private fun normalize(name: String?): String? {
        val n = name?.trim()?.lowercase() ?: return null
        if (n.isBlank()) return null
        return "hs_" + n.replace(Regex("[^a-z0-9]+"), "_").take(48)
    }

    companion object {
        private const val KEY_ENABLED = "remember_per_headset"
    }
}
