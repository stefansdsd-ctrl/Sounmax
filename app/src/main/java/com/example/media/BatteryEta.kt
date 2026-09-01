package com.example.media

import android.content.Context

object BatteryEta {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_SAMPLES = "battery_eta_samples"

    fun record(context: Context, percent: Int?) {
        if (percent == null || percent !in 0..100) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val raw = prefs.getString(KEY_SAMPLES, "") ?: ""
        val samples = raw.split('|').mapNotNull { part ->
            val bits = part.split(',')
            if (bits.size != 2) return@mapNotNull null
            val t = bits[0].toLongOrNull() ?: return@mapNotNull null
            val p = bits[1].toIntOrNull() ?: return@mapNotNull null
            t to p
        }.filter { now - it.first < 6 * 60 * 60 * 1000L }.toMutableList()
        val last = samples.lastOrNull()
        if (last == null || last.second != percent) {
            samples.add(now to percent)
        }
        val kept = samples.takeLast(12)
        prefs.edit().putString(KEY_SAMPLES, kept.joinToString("|") { "${it.first},${it.second}" }).apply()
    }

    fun etaMinutes(context: Context, currentPercent: Int?): Int? {
        if (currentPercent == null) return null
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SAMPLES, "") ?: return null
        val samples = raw.split('|').mapNotNull { part ->
            val bits = part.split(',')
            if (bits.size != 2) return@mapNotNull null
            val t = bits[0].toLongOrNull() ?: return@mapNotNull null
            val p = bits[1].toIntOrNull() ?: return@mapNotNull null
            t to p
        }
        if (samples.size < 2) return estimateFromTypical(currentPercent)
        val first = samples.first()
        val last = samples.last()
        val dtMin = ((last.first - first.first) / 60_000L).toInt().coerceAtLeast(1)
        val drop = first.second - last.second
        if (drop <= 0) return estimateFromTypical(currentPercent)
        val perHour = drop * 60f / dtMin
        if (perHour < 0.5f) return estimateFromTypical(currentPercent)
        return ((currentPercent / perHour) * 60f).toInt().coerceIn(5, 20 * 60)
    }

    private fun estimateFromTypical(percent: Int): Int =
        (percent / 100f * 28f * 60f).toInt().coerceAtLeast(5)

    fun label(minutes: Int?): String? {
        if (minutes == null) return null
        val h = minutes / 60
        val m = minutes % 60
        return if (h <= 0) "~${m}m accu" else "~${h}u${m}m accu"
    }
}
