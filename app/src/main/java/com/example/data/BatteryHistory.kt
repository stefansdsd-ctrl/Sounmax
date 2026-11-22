package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min

/**
 * History van headphone battery levels.
 * Bewaart laatste 50 metingen (level + timestamp).
 * ETA via lineaire regressie + nominale rates (ANC on ~40h, off ~80h voor TAH6519).
 * Trend: charging / discharging / stable.
 */
object BatteryHistory {
    private const val PREFS = "sounmax_bat_history"
    private const val KEY = "history_json"
    private const val MAX = 50
    private const val NOMINAL_HOURS_ANC_ON = 40f
    private const val NOMINAL_HOURS_ANC_OFF = 80f

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

    private fun isAncOn(context: Context): Boolean {
        val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        return prefs.getString("anc_mode", "off")?.contains("anc", ignoreCase = true) == true ||
               prefs.getBoolean("anc_active", false)
    }

    /** Estimated remaining hours until 0%.
     *  Combines linear regression with nominal capacity adjusted for ANC. */
    fun etaHours(context: Context, currentLevel: Int): Float {
        if (currentLevel <= 0) return 0f
        if (trend(context) == Trend.CHARGING) return -1f

        val ancOn = isAncOn(context)
        val nominal = if (ancOn) NOMINAL_HOURS_ANC_ON else NOMINAL_HOURS_ANC_OFF
        val nominalEta = (currentLevel / 100f) * nominal

        val recent = load(context).takeLast(20).filter { it.timestamp > 0 }
        if (recent.size < 5) return max(0f, min(48f, nominalEta))

        val t0 = recent.first().timestamp
        val points = recent.map { ((it.timestamp - t0) / 3_600_000.0) to it.level.toDouble() }
        val n = points.size.toDouble()
        val sumT = points.sumOf { it.first }
        val sumL = points.sumOf { it.second }
        val sumTL = points.sumOf { it.first * it.second }
        val sumT2 = points.sumOf { it.first * it.first }

        val denom = n * sumT2 - sumT * sumT
        if (denom == 0.0 || kotlin.math.abs(denom) < 1e-6) return max(0f, min(48f, nominalEta))
        val slope = (n * sumTL - sumT * sumL) / denom
        if (slope >= -0.05) return max(0f, min(48f, nominalEta))

        val regEta = currentLevel / (-slope)
        // Blend regression with nominal for robustness
        val blended = (regEta * 0.7 + nominalEta * 0.3).toFloat()
        return max(0f, min(48f, blended))
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
