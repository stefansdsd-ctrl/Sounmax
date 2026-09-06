package com.example.media

import android.content.Context
import android.content.SharedPreferences
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

object SceneScheduleAdvisor {
    const val KEY_ENABLED = "scene_schedule"
    private const val KEY_MORNING = "sched_morning_id"
    private const val KEY_WORK = "sched_work_id"
    private const val KEY_WEEKEND = "sched_weekend_id"
    private const val KEY_EVENING = "sched_evening_id"
    private const val KEY_NIGHT = "sched_night_id"
    const val KEY_MORNING_START = "sched_h_morning"
    const val KEY_WORK_START = "sched_h_work"
    const val KEY_EVENING_START = "sched_h_evening"
    const val KEY_NIGHT_START = "sched_h_night"

    private val DAY_SUFFIX = mapOf(
        Calendar.MONDAY to "ma",
        Calendar.TUESDAY to "di",
        Calendar.WEDNESDAY to "wo",
        Calendar.THURSDAY to "do",
        Calendar.FRIDAY to "vr",
        Calendar.SATURDAY to "za",
        Calendar.SUNDAY to "zo"
    )

    private val ALL_KEYS = listOf(KEY_MORNING, KEY_WORK, KEY_WEEKEND, KEY_EVENING, KEY_NIGHT) +
        listOf("ma", "di", "wo", "do", "vr", "za", "zo").flatMap {
            listOf("sched_work_${it}_id", "sched_weekend_${it}_id")
        }

    fun enabled(prefs: SharedPreferences): Boolean = prefs.getBoolean(KEY_ENABLED, false)
    fun setEnabled(prefs: SharedPreferences, on: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun hours(prefs: SharedPreferences): Hours {
        val morning = prefs.getInt(KEY_MORNING_START, 7).coerceIn(5, 10)
        val work = prefs.getInt(KEY_WORK_START, 9).coerceIn(morning + 1, 12)
        val evening = prefs.getInt(KEY_EVENING_START, 17).coerceIn(work + 1, 20)
        val night = prefs.getInt(KEY_NIGHT_START, 22).coerceIn(evening + 1, 23)
        return Hours(morning, work, evening, night)
    }

    fun cycleHours(prefs: SharedPreferences): String {
        val h = hours(prefs)
        val nextMorning = when (h.morning) { 6 -> 7; 7 -> 8; else -> 6 }
        val nextWork = when (h.work) { 8 -> 9; 9 -> 10; else -> 8 }
        val nextEvening = when (h.evening) { 16 -> 17; 17 -> 18; else -> 16 }
        val nextNight = when (h.night) { 21 -> 22; 22 -> 23; else -> 21 }
        prefs.edit()
            .putInt(KEY_MORNING_START, nextMorning)
            .putInt(KEY_WORK_START, nextWork.coerceAtLeast(nextMorning + 1))
            .putInt(KEY_EVENING_START, nextEvening.coerceAtLeast(nextWork + 1))
            .putInt(KEY_NIGHT_START, nextNight.coerceAtLeast(nextEvening + 1))
            .apply()
        return label(prefs)
    }

    fun adjust(context: Context, current: ListeningScene): ListeningScene {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!enabled(prefs)) return current
        val slot = currentSlot(prefs) ?: return current
        val id = resolveSceneId(prefs, slot) ?: return current
        return SceneLookup.byId(id) ?: current
    }

    fun pinCurrent(prefs: SharedPreferences, sceneId: String): String {
        val slot = currentSlot(prefs)
        if (slot == null) {
            setEnabled(prefs, false)
            return "Schema: buiten slots (uit)"
        }
        val existing = resolveSceneId(prefs, slot)
        return if (existing == sceneId && enabled(prefs)) {
            prefs.edit().remove(slot.key).remove(slot.fallback)
                .putBoolean(KEY_ENABLED, hasAny(prefs, except = slot.key)).apply()
            "Schema ${slot.label} gewist"
        } else {
            prefs.edit().putString(slot.key, sceneId).putBoolean(KEY_ENABLED, true).apply()
            "Schema ${slot.label} -> $sceneId"
        }
    }

    fun label(prefs: SharedPreferences): String {
        val h = hours(prefs)
        if (!enabled(prefs)) return "Tijdschema uit · ${h.short()}"
        val slot = currentSlot(prefs) ?: return "Tijdschema (buiten slot) · ${h.short()}"
        val id = resolveSceneId(prefs, slot) ?: return "Tijdschema ${slot.label}: leeg · ${h.short()}"
        return "Tijdschema ${slot.label}: $id · ${h.short()}"
    }

    private fun resolveSceneId(prefs: SharedPreferences, slot: Slot): String? =
        prefs.getString(slot.key, null)?.takeIf { it.isNotBlank() }
            ?: prefs.getString(slot.fallback, null)?.takeIf { it.isNotBlank() }

    private fun hasAny(prefs: SharedPreferences, except: String): Boolean =
        ALL_KEYS.any { it != except && !prefs.getString(it, null).isNullOrBlank() }

    private data class Slot(val key: String, val fallback: String, val label: String)
    data class Hours(val morning: Int, val work: Int, val evening: Int, val night: Int) {
        fun short(): String = "%02d/%02d/%02d/%02d".format(morning, work, evening, night)
    }

    private fun currentSlot(prefs: SharedPreferences): Slot? {
        val h = hours(prefs)
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
        val day = DAY_SUFFIX[dow] ?: "ma"
        return when {
            hour >= h.night || hour < h.morning -> Slot(KEY_NIGHT, KEY_NIGHT, "nacht ${h.night}-${h.morning}")
            hour in h.morning until h.work -> Slot(KEY_MORNING, KEY_MORNING, "ochtend ${h.morning}-${h.work}")
            hour in h.work until h.evening && weekend -> Slot("sched_weekend_${day}_id", KEY_WEEKEND, "weekend $day ${h.work}-${h.evening}")
            hour in h.work until h.evening -> Slot("sched_work_${day}_id", KEY_WORK, "werk $day ${h.work}-${h.evening}")
            hour in h.evening until h.night -> Slot(KEY_EVENING, KEY_EVENING, "avond ${h.evening}-${h.night}")
            else -> null
        }
    }
}
