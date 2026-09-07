package com.example.media

import android.content.SharedPreferences
import com.example.dsp.ListeningScene

object SceneReason {
    const val KEY = "last_scene_reason"
    const val KEY_PREV = "ab_scene_id"

    fun step(
        before: ListeningScene,
        after: ListeningScene,
        why: String,
        reasons: MutableList<String>
    ): ListeningScene {
        if (after.id != before.id) reasons += why
        return after
    }

    fun save(prefs: SharedPreferences, reasons: List<String>, manual: String? = null) {
        val text = manual ?: if (reasons.isEmpty()) "Tijd/dosis-suggestie" else reasons.joinToString(" · ")
        prefs.edit().putString(KEY, text).apply()
    }

    fun read(prefs: SharedPreferences): String {
        val base = prefs.getString(KEY, null)?.takeIf { it.isNotBlank() }
        val fusion = prefs.getString("auto_fusion_reasons", null)?.takeIf { it.isNotBlank() }
        val noise = prefs.getString("last_noise_suggest_reason", null)?.takeIf { it.isNotBlank() }
        val parts = listOfNotNull(base, fusion, noise).distinct()
        return parts.joinToString(" · ").ifBlank { "Handmatig of standaard" }
    }
}
