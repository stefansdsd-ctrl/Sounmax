package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min

/**
 * History van headphone battery levels.
 * Bewaart laatste 50 metingen (level + timestamp).
 * ETA via lineaire regressie op recente ontlading.
 * Trend: charging / discharging / stable.
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

    /** Estimated remaining hours until 0% using linear regression on recent discharge. */
    fun etaHours(context: Context, currentLevel: Int): Float {
        if (currentLevel <= 0) return 0f
        if (trend(context) == Trend.CHARGING) return -1f
        val recent = load(context).takeLast(15).filter { it.timestamp > 0 }
        if (recent.size < 4) return -1f

        // Linear regression: time (hours from first) vs level
        val t0 = recent.first().timestamp
        val points = recent.map { ((it.timestamp - t0) / 3_600_000.0) to it.level.toDouble() }
        val n = points.size.toDouble()
        val sumT = points.sumOf { it.first }
        val sumL = points.sumOf { it.second }
        val sumTL = points.sumOf { it.first * it.second }
        val sumT2 = points.sumOf { it.first * it.first }

        val denom = n * sumT2 - sumT * sumT
        if (denom == 0.0) return -1f
        val slope = (n * sumTL - sumT * sumL) / denom // % per hour
        if (slope >= -0.1) return -1f // not discharging enough

        val eta = currentLevel / (-slope)
        return max(0f, min(48f, eta.toFloat())) // cap at 48h
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
