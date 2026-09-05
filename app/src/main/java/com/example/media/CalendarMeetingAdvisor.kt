package com.example.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Schakelt meeting/videocall in als er nu een agenda-event loopt. */
object CalendarMeetingAdvisor {
    const val KEY_ENABLED = "calendar_advisor"

    private val VIDEO = listOf(
        "meet", "zoom", "teams", "webex", "facetime", "hangout", "videocall", "video call"
    )
    private val MEET = listOf(
        "meeting", "vergadering", "overleg", "call", "1:1", "standup", "stand-up",
        "sync", "interview", "afspraak", "1-op-1"
    )

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) return scene
        val kind = currentEventKind(context) ?: return scene
        val id = if (kind == "video") "videocall" else "meeting"
        return SceneLookup.byId(id) ?: scene
    }

    private fun currentEventKind(context: Context): String? {
        val now = System.currentTimeMillis()
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(now.toString())
            .appendPath((now + 2 * 60_000L).toString())
            .build()
        val cols = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.AVAILABILITY,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
        )
        return try {
            context.contentResolver.query(uri, cols, null, null, null)?.use { c ->
                val tIdx = c.getColumnIndex(CalendarContract.Instances.TITLE)
                val aIdx = c.getColumnIndex(CalendarContract.Instances.ALL_DAY)
                val vIdx = c.getColumnIndex(CalendarContract.Instances.AVAILABILITY)
                val bIdx = c.getColumnIndex(CalendarContract.Instances.BEGIN)
                val eIdx = c.getColumnIndex(CalendarContract.Instances.END)
                while (c.moveToNext()) {
                    if (aIdx >= 0 && c.getInt(aIdx) == 1) continue
                    if (vIdx >= 0 && c.getInt(vIdx) == CalendarContract.Events.AVAILABILITY_FREE) continue
                    val begin = if (bIdx >= 0) c.getLong(bIdx) else 0L
                    val end = if (eIdx >= 0) c.getLong(eIdx) else 0L
                    if (now < begin - 3 * 60_000L || now > end) continue
                    val title = (if (tIdx >= 0) c.getString(tIdx) else "")?.lowercase().orEmpty()
                    if (VIDEO.any { title.contains(it) }) return "video"
                    if (title.isBlank() || MEET.any { title.contains(it) } || title.length > 2) return "meet"
                }
            }
            null
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}
