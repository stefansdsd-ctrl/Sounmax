package com.example.data

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService

/**
 * Nu-spelend via actieve MediaSession.
 * Vereist NotificationListenerService enabled voor volledige toegang.
 * Chip: titel · artiest · progress. Album art via metadata bitmap/uri (UI-side).
 */
object MediaNowPlaying {
    private const val PREFS = "sounmax_now_playing"

    data class Track(val title: String, val artist: String, val position: Long, val duration: Long, val playing: Boolean)

    fun current(context: Context): Track? {
        val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager ?: return null
        val component = ComponentName(context, SounmaxNotiListener::class.java)
        return try {
            val controllers = msm.getActiveSessions(component)
            controllers.firstOrNull()?.let { c ->
                val meta = c.metadata
                val state = c.playbackState
                Track(
                    title = meta?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "",
                    artist = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "",
                    position = state?.position ?: 0L,
                    duration = meta?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L,
                    playing = state?.state == PlaybackState.STATE_PLAYING
                )
            }
        } catch (_: SecurityException) {
            null // listener not enabled
        }
    }

    fun chip(context: Context): String {
        val t = current(context) ?: return "Nu: —"
        if (t.title.isBlank()) return "Nu: —"
        val prog = if (t.duration > 0) " ${(t.position / 1000)}/${(t.duration / 1000)}s" else ""
        val play = if (t.playing) "▶" else "⏸"
        return "$play ${t.title.take(18)} · ${t.artist.take(12)}$prog"
    }

    fun albumArt(controller: MediaController?): android.graphics.Bitmap? =
        controller?.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: controller?.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
}

/** Stub listener — user moet inschakelen in Notificatie-toegang. */
class SounmaxNotiListener : NotificationListenerService()
