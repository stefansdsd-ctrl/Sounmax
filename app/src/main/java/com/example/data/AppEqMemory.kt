package com.example.data

import android.content.Context
import com.example.dsp.EqPreset

data class AppEqBinding(
    val packageName: String,
    val presetName: String,
    val label: String
)

class AppEqMemory(context: Context) {
    private val prefs = context.getSharedPreferences("soundmax_app_eq", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean("enabled", true)
        set(value) { prefs.edit().putBoolean("enabled", value).apply() }

    fun save(packageName: String?, preset: EqPreset, bands: List<Float>) {
        val pkg = packageName?.takeIf { it.isNotBlank() } ?: return
        prefs.edit()
            .putString("$pkg.name", preset.name)
            .putString("$pkg.bands", bands.joinToString(","))
            .putInt("$pkg.bass", preset.bassBoost)
            .putInt("$pkg.virt", preset.virtualizer)
            .putInt("$pkg.loud", preset.loudness)
            .putFloat("$pkg.clarity", preset.clarity)
            .apply()
    }

    fun load(packageName: String?, ignoreEnabled: Boolean = false): EqPreset? {
        if (!enabled && !ignoreEnabled) return null
        val pkg = packageName?.takeIf { it.isNotBlank() } ?: return null
        val name = prefs.getString("$pkg.name", null) ?: return null
        val bands = prefs.getString("$pkg.bands", null)
            ?.split(",")
            ?.mapNotNull { it.toFloatOrNull() }
            ?: return null
        if (bands.size != 10) return null
        return EqPreset(
            name = name,
            bandGains = bands,
            bassBoost = prefs.getInt("$pkg.bass", 0),
            virtualizer = prefs.getInt("$pkg.virt", 0),
            loudness = prefs.getInt("$pkg.loud", 0),
            clarity = prefs.getFloat("$pkg.clarity", 0f),
            isCustom = true,
            category = "Per-app",
            description = "EQ voor $pkg"
        )
    }

    fun delete(packageName: String?) {
        val pkg = packageName?.takeIf { it.isNotBlank() } ?: return
        prefs.edit()
            .remove("$pkg.name")
            .remove("$pkg.bands")
            .remove("$pkg.bass")
            .remove("$pkg.virt")
            .remove("$pkg.loud")
            .remove("$pkg.clarity")
            .apply()
    }

    fun listBindings(): List<AppEqBinding> {
        return prefs.all.keys
            .filter { it.endsWith(".name") }
            .mapNotNull { key ->
                val pkg = key.removeSuffix(".name")
                val name = prefs.getString(key, null) ?: return@mapNotNull null
                AppEqBinding(pkg, name, label(pkg))
            }
            .sortedBy { it.label.lowercase() }
    }

    fun label(packageName: String?): String {
        if (packageName.isNullOrBlank()) return "geen app"
        return when {
            packageName.contains("youtube.music") -> "YouTube Music"
            packageName.contains("spotify") -> "Spotify"
            packageName.contains("tidal") -> "Tidal"
            packageName.contains("soundcloud") -> "SoundCloud"
            packageName.contains("apple.android.music") -> "Apple Music"
            packageName.endsWith(".android.music") -> "Muziek"
            else -> packageName.substringAfterLast('.')
        }
    }
}
