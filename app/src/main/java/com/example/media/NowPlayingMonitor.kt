package com.example.media

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.AppEqMemory
import com.example.data.NowPlayingApp

data class NowPlayingTrack(
    val title: String,
    val artist: String,
    val genre: String,
    val packageName: String
)

class NowPlayingMonitor(
    private val context: Context,
    private val onTrack: (NowPlayingTrack) -> Unit
) {
    private val tag = "NowPlayingMonitor"
    private val handler = Handler(Looper.getMainLooper())
    private val listenerComponent = ComponentName(context, SoundMaxNotificationListener::class.java)
    private var manager: MediaSessionManager? = null
    private var lastKey: String? = null
    private val appEqMemory = AppEqMemory(context)

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        attach(controllers.orEmpty())
    }

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) = publish(activeController())
        override fun onPlaybackStateChanged(state: PlaybackState?) = publish(activeController())
    }

    fun start() {
        try {
            manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            manager?.addOnActiveSessionsChangedListener(sessionListener, listenerComponent, handler)
            attach(manager?.getActiveSessions(listenerComponent).orEmpty())
        } catch (e: SecurityException) {
            Log.i(tag, "Meldingsstoegang nodig voor Now Playing: ${e.message}")
        } catch (e: Exception) {
            Log.w(tag, "Now Playing start: ${e.message}")
        }
    }

    fun stop() {
        try {
            manager?.removeOnActiveSessionsChangedListener(sessionListener)
        } catch (_: Exception) {}
        manager = null
        lastKey = null
    }

    fun hasAccess(): Boolean {
        return try {
            val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            msm.getActiveSessions(listenerComponent)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun attach(controllers: List<MediaController>) {
        controllers.forEach { c ->
            try { c.unregisterCallback(callback) } catch (_: Exception) {}
            try { c.registerCallback(callback, handler) } catch (_: Exception) {}
        }
        publish(controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING } ?: controllers.firstOrNull())
    }

    private fun activeController(): MediaController? {
        return try {
            val list = manager?.getActiveSessions(listenerComponent).orEmpty()
            list.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING } ?: list.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    private fun publish(controller: MediaController?) {
        val md = controller?.metadata ?: return
        val title = md.getString(MediaMetadata.METADATA_KEY_TITLE).orEmpty()
        val artist = md.getString(MediaMetadata.METADATA_KEY_ARTIST).orEmpty()
        val genre = md.getString(MediaMetadata.METADATA_KEY_GENRE).orEmpty()
        if (title.isBlank()) return
        val key = "${controller.packageName}|$title|$artist"
        if (key == lastKey) return
        lastKey = key
        NowPlayingApp.packageName = controller.packageName
        NowPlayingApp.title = title
        NowPlayingApp.artist = artist
        NowPlayingApp.genre = genre
        appEqMemory.load(controller.packageName)?.let { NowPlayingApp.onBoundPreset?.invoke(it) }
        onTrack(NowPlayingTrack(title, artist, genre, controller.packageName))
    }
}
