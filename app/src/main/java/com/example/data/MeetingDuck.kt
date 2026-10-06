package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat

/**
 * Agenda-demp: afspraak start binnen 12 min → muziek naar 35%.
 * Vereist READ_CALENDAR (al aangevraagd in MainActivity).
 */
object MeetingDuck {
    private const val PREFS = "sounmax_meeting_duck"
    private const val WINDOW_MS = 12 * 60 * 1000L
    private const val CAP_PCT = 35
    private const val TRIGGER_PCT = 45

    fun enabled(context: Context) = prefs(context).getBoolean("on", false)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context) else prefs(context).edit().putBoolean("ducked", false).apply()
        return label(context)
    }

    fun minutesUntil(context: Context): Int? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) return null
        val now = System.currentTimeMillis()
        val end = now + WINDOW_MS
        val projection = arrayOf(CalendarContract.Events.DTSTART, CalendarContract.Events.TITLE)
        val sel = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        return try {
            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                sel,
                arrayOf(now.toString(), end.toString()),
                "${CalendarContract.Events.DTSTART} ASC"
            )?.use { c ->
                if (!c.moveToFirst()) return null
                val start = c.getLong(0)
                ((start - now + 59_999L) / 60_000L).toInt().coerceAtLeast(0)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun apply(context: Context): Boolean {
        val mins = minutesUntil(context)
        if (!enabled(context) || mins == null) {
            prefs(context).edit().putBoolean("ducked", false).apply()
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putBoolean("ducked", true).putInt("mins", mins).apply()
            return true
        }
        return false
    }

    fun label(context: Context): String {
        val mins = minutesUntil(context)
        return when {
            !enabled(context) -> "Agenda-demp uit"
            mins != null && prefs(context).getBoolean("ducked", false) -> "Agenda-demp ${mins}m"
            mins != null -> "Afspraak over ${mins}m"
            else -> "Agenda-demp aan"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
