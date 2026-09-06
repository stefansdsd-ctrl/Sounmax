package com.example.media

import android.content.Context
import android.content.SharedPreferences
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/**
 * Vaste tijdslots die auto-scene overschrijven.
 * Slots: ochtend 07–09, werk 09–17, avond 17–22.
 */
object SceneScheduleAdvisor {
    const val KEY_ENABLED = "scene_schedule"
    private const val KEY_MORNING = "sched_morning_id"
    private const val KEY_WORK = "sched_work_id"
    private const val KEY_EVENING = "sched_evening_id"

    fun enabled(prefs: SharedPreferences): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun setEnabled(prefs: SharedPreferences, on: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun adjust(context: Context, current: ListeningScene): ListeningScene {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!enabled(prefs)) return current
        val slot = currentSlot() ?: return current
        val id = prefs.getString(slot.key, null) ?: return current
        return SceneLookup.byId(id) ?: current
    }

    fun pinCurrent(prefs: SharedPreferences, sceneId: String): String {
        val slot = currentSlot()
        if (slot == null) {
            setEnabled(prefs, false)
            return "Schema: buiten slots (uit)"
        }
        val existing = prefs.getString(slot.key, null)
        return if (existing == sceneId && enabled(prefs)) {
            prefs.edit().remove(slot.key).putBoolean(KEY_ENABLED, hasAny(prefs, except = slot.key)).apply()
            "Schema ${slot.label} gewist"
        } else {
            prefs.edit().putString(slot.key, sceneId).putBoolean(KEY_ENABLED, true).apply()
            "Schema ${slot.label} → $sceneId"
        }
    }

    fun label(prefs: SharedPreferences): String {
        if (!enabled(prefs)) return "Tijdschema uit"
        val slot = currentSlot() ?: return "Tijdschema (buiten slot)"
        val id = prefs.getString(slot.key, null) ?: return "Tijdschema ${slot.label}: leeg"
        return "Tijdschema ${slot.label}: $id"
    }

    private fun hasAny(prefs: SharedPreferences, except: String): Boolean =
        listOf(KEY_MORNING, KEY_WORK, KEY_EVENING).any { it != except && !prefs.getString(it, null).isNullOrBlank() }

    private data class Slot(val key: String, val label: String)

    private fun currentSlot(): Slot? {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 7 until 9 -> Slot(KEY_MORNING, "ochtend")
            in 9 until 17 -> Slot(KEY_WORK, "werk")
            in 17 until 22 -> Slot(KEY_EVENING, "avond")
            else -> null
        }
    }
}
