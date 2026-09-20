package com.example.media

import android.content.Context

/** Continu-sessie: na 60 min luisteren pauze voorstellen. */
object SessionBreak {
    private const val PREFS = "sounmax_session_break"
    const val BREAK_AFTER_MIN = 60

    fun tick(context: Context, playing: Boolean, volumePct: Int) {
        val p = prefs(context)
        val now = System.currentTimeMillis()
        if (!playing) {
            p.edit().putLong("start", 0L).apply()
            return
        }
        val start = p.getLong("start", 0L)
        if (start == 0L) {
            p.edit().putLong("start", now).apply()
            return
        }
        val mins = ((now - start) / 60_000L).toInt()
        if (mins > 0) ListenDose.record(context, volumePct, 0)
        p.edit().putInt("mins", mins).apply()
    }

    fun shouldBreak(context: Context): Boolean =
        prefs(context).getInt("mins", 0) >= BREAK_AFTER_MIN

    fun hint(context: Context): String? {
        val m = prefs(context).getInt("mins", 0)
        return if (m >= BREAK_AFTER_MIN)
            "Al $m minuten achter elkaar. 5 min zonder koptelefoon scheelt oormoeheid."
        else null
    }

    fun reset(context: Context) {
        prefs(context).edit().putLong("start", 0L).putInt("mins", 0).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
