package com.example.data

import android.content.Context

/** Onthoudt laatste slaaptimer-minuten per scene. */
object SceneSleepMemory {
    private const val PREFS = "sounmax_scene_sleep"
    private const val LAST = "last_minutes"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun minutesFor(context: Context, sceneId: String?, fallback: Int = 30): Int {
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return prefs(context).getInt(LAST, fallback)
        return prefs(context).getInt("m_$id", prefs(context).getInt(LAST, fallback))
    }

    fun save(context: Context, sceneId: String?, minutes: Int) {
        val m = minutes.coerceIn(1, 180)
        val e = prefs(context).edit().putInt(LAST, m)
        sceneId?.takeIf { it.isNotBlank() }?.let { e.putInt("m_$it", m) }
        e.apply()
    }
}
