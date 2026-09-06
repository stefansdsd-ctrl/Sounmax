package com.example.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/**
 * Schakelt meeting/videocall in bij een echt overleg.
 * 5 min voor start al actief. Geen willekeurige afspraken meer
 * (voorheen gold bijna elke titel > 2 tekens als meeting).
 */
object CalendarMeetingAdvisor {
    const val KEY_ENABLED = "calendar_advisor"
    const val KEY_LAST_TITLE = "calendar_last_title"
    private const val PRE_ROLL_MS = 5 * 60_000L

    private val VIDEO = listOf(
        "zoom", "webex", "facetime", "hangout", "hangouts",
        "videocall", "video call", "video-call", "jitsi", "whereby", "around.co",
        "meet.google", "teams.microsoft", "zoom.us", "google meet"
    )
    private val VIDEO_WORDS = listOf("meet", "teams")
    private val MEET = listOf(
        "meeting", "vergadering", "overleg", "call", "1:1", "1/1", "standup",
        "stand-up", "stand up", "sync", "interview", "afspraak", "1-op-1",
        "1op1", "retrospective", "retro", "sprint", "kickoff", "kick-off",
        "workshop", "demo", "all-hands", "allhands", "1 on 1"
    )

    @Volatile
    var lastEventTitle: String? = null
        private set

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        lastEventTitle = null
        if (!enabled(context)) return scene
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) return scene
        val hit = currentEvent(context) ?: return scene
        lastEventTitle = hit.title
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LAST_TITLE, hit.title).apply()
        val id = if (hit.video) "videocall" else "meeting"
        return SceneLookup.byId(id) ?: scene
    }

    fun reasonLabel(): String {
        val t = lastEventTitle?.trim().orEmpty()
        return if (t.isNotEmpty()) "agenda: $t" else "agenda"
    }

    private data class Hit(val title: String, val video: Boolean)

    private fun currentEvent(context: Context): Hit? {
        val now = System.currentTimeMillis()
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath((now - PRE_ROLL_MS).toString())
            .appendPath((now + 2 * 60_000L).toString())
            .build()
        val cols = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.AVAILABILITY,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
        )
        return try {
            context.contentResolver.query(uri, cols, null, null, null)?.use { c ->
                val tIdx = c.getColumnIndex(CalendarContract.Instances.TITLE)
                val dIdx = c.getColumnIndex(CalendarContract.Instances.DESCRIPTION)
                val lIdx = c.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)
                val aIdx = c.getColumnIndex(CalendarContract.Instances.ALL_DAY)
                val vIdx = c.getColumnIndex(CalendarContract.Instances.AVAILABILITY)
                val bIdx = c.getColumnIndex(CalendarContract.Instances.BEGIN)
                val eIdx = c.getColumnIndex(CalendarContract.Instances.END)
                var best: Hit? = null
                while (c.moveToNext()) {
                    if (aIdx >= 0 && c.getInt(aIdx) == 1) continue
                    if (vIdx >= 0 && c.getInt(vIdx) == CalendarContract.Events.AVAILABILITY_FREE) continue
                    val begin = if (bIdx >= 0) c.getLong(bIdx) else 0L
                    val end = if (eIdx >= 0) c.getLong(eIdx) else 0L
                    if (now < begin - PRE_ROLL_MS || (end > 0L && now > end)) continue
                    val title = (if (tIdx >= 0) c.getString(tIdx) else "")?.lowercase().orEmpty()
                    val desc = (if (dIdx >= 0) c.getString(dIdx) else "")?.lowercase().orEmpty()
                    val loc = (if (lIdx >= 0) c.getString(lIdx) else "")?.lowercase().orEmpty()
                    val blob = "$title\n$desc\n$loc"
                    val video = VIDEO.any { blob.contains(it) } ||
                        VIDEO_WORDS.any { w -> blob.contains(Regex("(?<![a-z0-9])$w(?![a-z0-9])")) }
                    val meet = MEET.any { title.contains(it) } || video
                    if (!meet && !video) continue
                    val pretty = (if (tIdx >= 0) c.getString(tIdx) else null)
                        ?.trim()?.take(48).orEmpty()
                    val hit = Hit(pretty.ifBlank { if (video) "Videogesprek" else "Vergadering" }, video)
                    if (video) return hit
                    if (best == null) best = hit
                }
                best
            }
        } catch (_: SecurityException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}
