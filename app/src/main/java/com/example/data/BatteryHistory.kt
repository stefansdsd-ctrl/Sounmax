package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

/**
 * Simpele history van headphone battery levels.
 * Bewaart laatste 50 metingen (level + timestamp).
 * + ETA remaining hours based on recent discharge rate.
 * + Trend: charging / discharging / stable.
 */
object BatteryHistory {
    private const val PREFS = "sounmax_bat_history"
    private const val KEY = "history_json"
    private const val MAX = 50

    enum class Trend { CHARGING, DISCHARGING, STABLE, UNKNOWN }

    fun record(context: Context, level: Int) {
        if (level !in 0..100) return
        val list = load(context).toMutableList()
        list.add(Entry(level, System.currentTimeMillis()))
        if (list.size > MAX) list.removeAt(0)
        save(context, list)
    }

    fun load(context: Context): List<Entry> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                Entry(obj.optInt("l", -1), obj.optLong("t", 0))
            }.filter { it.level in 0..100 }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun averageLast(context: Context, n: Int = 5): Float {
        val recent = load(context).takeLast(n)
        return if (recent.isEmpty()) -1f else recent.map { it.level }.average().toFloat()
    }

    /** Trend over last measurements. */
    fun trend(context: Context): Trend {
        val recent = load(context).takeLast(6)
        if (recent.size < 3) return Trend.UNKNOWN
        val first = recent.first().level
        val last = recent.last().level
        val delta = last - first
        return when {
            delta >= 3 -> Trend.CHARGING
            delta <= -3 -> Trend.DISCHARGING
            else -> Trend.STABLE
        }
    }

    /** Estimated remaining hours until 0% based on recent discharge. Returns -1 if unknown. */
    fun etaHours(context: Context, currentLevel: Int): Float {
        if (currentLevel <= 0) return 0f
        if (trend(context) == Trend.CHARGING) return -1f // charging, no ETA
        val recent = load(context).takeLast(10)
        if (recent.size < 3) return -1f
        val first = recent.first()
        val last = recent.last()
        val deltaLevel = first.level - last.level
        val deltaHours = (last.timestamp - first.timestamp) / 3_600_000f
        if (deltaHours < 0.1f || deltaLevel <= 0) return -1f
        val rate = deltaLevel / deltaHours // % per hour
        return max(0f, currentLevel / rate)
    }

    private fun save(context: Context, entries: List<Entry>) {
        val arr = JSONArray()
        entries.forEach {
            arr.put(JSONObject().put("l", it.level).put("t", it.timestamp))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, arr.toString()).apply()
    }

    data class Entry(val level: Int, val timestamp: Long)
}
