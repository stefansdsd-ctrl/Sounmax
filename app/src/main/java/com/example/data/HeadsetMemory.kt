package com.example.data

import android.content.Context
import com.example.dsp.EqPreset

/** Onthoudt EQ + scene per Bluetooth-MAC, met naam als fallback. */
class HeadsetMemory(context: Context) {
    private val prefs = context.getSharedPreferences("soundmax_headset_memory", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) { prefs.edit().putBoolean(KEY_ENABLED, value).apply() }

    fun save(headsetName: String?, preset: EqPreset, bands: List<Float>, address: String? = null) {
        if (!enabled) return
        val key = keyOf(address, headsetName) ?: return
        prefs.edit()
            .putString("$key.name", preset.name)
            .putString("$key.bands", bands.joinToString(","))
            .putInt("$key.bass", preset.bassBoost)
            .putInt("$key.virt", preset.virtualizer)
            .putInt("$key.loud", preset.loudness)
            .putFloat("$key.clarity", preset.clarity)
            .putString("$key.display", headsetName)
            .putString("$key.mac", address?.uppercase())
            .putLong("$key.ts", System.currentTimeMillis())
            .apply()
        if (!address.isNullOrBlank() && !headsetName.isNullOrBlank()) {
            save(headsetName, preset, bands, address = null)
        }
    }

    fun saveScene(headsetName: String?, sceneId: String?, address: String? = null) {
        if (!enabled) return
        val key = keyOf(address, headsetName) ?: return
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        prefs.edit().putString("$key.scene", id).apply()
    }

    fun loadScene(headsetName: String?, address: String? = null): String? {
        if (!enabled) return null
        keyOf(address, headsetName)?.let { k ->
            prefs.getString("$k.scene", null)?.let { return it }
        }
        return keyOf(null, headsetName)?.let { prefs.getString("$it.scene", null) }
    }

    fun load(headsetName: String?, address: String? = null): EqPreset? {
        if (!enabled) return null
        return loadKey(keyOf(address, headsetName), headsetName)
            ?: loadKey(keyOf(null, headsetName), headsetName)
    }

    fun isTah(name: String?, address: String? = null): Boolean {
        val n = (name ?: "").lowercase()
        if (n.contains("tah6519") || n.contains("philips") && n.contains("tah")) return true
        val key = keyOf(address, name) ?: return false
        return prefs.getString("$key.display", "")?.lowercase()?.contains("tah6519") == true
    }

    private fun loadKey(key: String?, headsetName: String?): EqPreset? {
        if (key == null) return null
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
            description = "Laatst gebruikt op ${headsetName ?: prefs.getString("$key.display", "headset")}"
        )
    }

    private fun keyOf(address: String?, name: String?): String? {
        val mac = address?.uppercase()?.replace(":", "")?.filter { it.isLetterOrDigit() }
        if (!mac.isNullOrBlank() && mac.length >= 8) return "mac_$mac"
        return normalize(name)
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
