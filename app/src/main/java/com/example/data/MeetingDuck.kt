package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Agenda-demp: afspraak start binnen 12 min → muziek stapsgewijs naar 35%.
 * Deelt DuckLane. Laagste cap wint. Vereist READ_CALENDAR.
 */
object MeetingDuck {
    private const val PREFS = "sounmax_meeting_duck"
    private const val WINDOW_MS = 12 * 60 * 1000L
    private const val CAP_PCT = 35
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", false)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "agenda")
        else apply(context)
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (soon(app)) apply(app) else tick(app)
                handler.postDelayed(this, 15_000)
            }
        }
        handler.post(loop)
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

    fun soon(context: Context): Boolean = enabled(context) && minutesUntil(context) != null

    fun apply(context: Context): Boolean {
        if (!soon(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) return false
        if (!am.isMusicActive) return false
        return DuckLane.hold(context, "agenda", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (soon(context)) return false
        if (!DuckLane.heldBy(context, "agenda") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "agenda")
    }

    fun active(context: Context): Boolean =
        soon(context) && (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context): String {
        val mins = minutesUntil(context)
        return when {
            !enabled(context) -> "Agenda-demp uit"
            active(context) && mins != null -> "Agenda, muziek 35% (${mins}m)"
            mins != null -> "Afspraak over ${mins}m"
            DuckLane.restoring(context) -> "Volume komt terug"
            else -> "Agenda-demp aan"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
