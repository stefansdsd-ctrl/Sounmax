package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.time.LocalDate
import java.time.LocalTime

/**
 * Onthoudt de eerste scene van de dag tussen 06:30–09:30.
 * Volgende ochtend: "Gisteren pendelen: X".
 */
class CommuteMemory(context: Context) {
    private val prefs = context.getSharedPreferences("sounmax_commute", Context.MODE_PRIVATE)

    fun onScene(sceneId: String?) {
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        val now = LocalTime.now()
        if (now.isBefore(WINDOW_START) || now.isAfter(WINDOW_END)) return
        val today = LocalDate.now().toString()
        if (prefs.getString(KEY_DATE, null) == today) return
        prefs.edit()
            .putString(KEY_DATE, today)
            .putString(KEY_SCENE, id)
            .apply()
    }

    fun suggestLabel(): String? {
        val today = LocalDate.now()
        val now = LocalTime.now()
        if (now.isBefore(WINDOW_START) || now.isAfter(WINDOW_END)) return null
        val storedDate = prefs.getString(KEY_DATE, null) ?: return null
        val storedScene = prefs.getString(KEY_SCENE, null) ?: return null
        val yesterday = today.minusDays(1).toString()
        if (storedDate != yesterday && storedDate != today) return null
        if (storedDate == today) return null
        val scene = SceneLookup.byId(storedScene) ?: return null
        return "Gisteren pendelen: ${scene.name}"
    }

    fun suggestedScene(): ListeningScene? {
        suggestLabel() ?: return null
        return SceneLookup.byId(prefs.getString(KEY_SCENE, null))
    }

    companion object {
        private val WINDOW_START = LocalTime.of(6, 30)
        private val WINDOW_END = LocalTime.of(9, 30)
        private const val KEY_DATE = "date"
        private const val KEY_SCENE = "scene"
    }
}
