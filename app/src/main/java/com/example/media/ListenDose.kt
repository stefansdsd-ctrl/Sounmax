package com.example.media

import android.content.Context
import java.util.Calendar

/**
 * Wekelijkse luisterdosis: som (relatief volume × minuten) per dag.
 * Suggestie pauze als 7-daags > 480 minuten-équivalent op ≥ 70% volume.
 */
object ListenDose {
    private const val PREFS = "sounmax_listen_dose"
    const val PAUSE_THRESHOLD_MIN = 480

    fun record(context: Context, volumePct: Int, minutes: Int = 1) {
        if (minutes <= 0) return
        val key = dayKey()
        val add = minutes * (volumePct.coerceIn(0, 100) / 100f)
        val p = prefs(context)
        p.edit().putFloat(key, p.getFloat(key, 0f) + add).apply()
        prune(context)
    }

    fun weekMinutes(context: Context): List<Pair<String, Float>> =
        (6 downTo 0).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val key = dayKey(cal)
            key to prefs(context).getFloat(key, 0f)
        }

    fun weekTotal(context: Context): Float = weekMinutes(context).sumOf { it.second.toDouble() }.toFloat()

    fun shouldPause(context: Context): Boolean = weekTotal(context) >= PAUSE_THRESHOLD_MIN

    fun pauseHint(context: Context): String? =
        if (shouldPause(context)) {
            "Veel luisteruren deze week (≈ ${weekTotal(context).toInt()} min eq.). Neem een pauze of zet veilig volume aan."
        } else null

    private fun dayKey(cal: Calendar = Calendar.getInstance()): String {
        val y = cal.get(Calendar.YEAR)
        val d = cal.get(Calendar.DAY_OF_YEAR)
        return "d_$y-$d"
    }

    private fun prune(context: Context) {
        val keep = weekMinutes(context).map { it.first }.toSet()
        val p = prefs(context)
        val stale = p.all.keys.filter { it.startsWith("d_") && it !in keep }
        if (stale.isNotEmpty()) {
            val e = p.edit()
            stale.forEach { e.remove(it) }
            e.apply()
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
