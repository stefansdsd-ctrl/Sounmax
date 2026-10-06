package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Onthoudt het muziekvolume vóór de mic-cap en bouwt het stapsgewijs terug
 * zodra de andere app stopt met opnemen. Geen sprong terug naar 100%.
 */
object MicRestore {
    private const val PREFS = "sounmax_mic_restore"
    private const val STEP = 2

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) clear(context)
        return label(context)
    }

    fun remember(context: Context) {
        if (!enabled(context)) return
        val p = prefs(context)
        if (p.getInt("saved", -1) >= 0) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        p.edit().putInt("saved", cur).putLong("since", System.currentTimeMillis()).apply()
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context)) return false
        val p = prefs(context)
        val saved = p.getInt("saved", -1)
        if (saved < 0) return false
        if (MicLive.recording(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur >= saved) {
            clear(context)
            return false
        }
        val next = (cur + STEP).coerceAtMost(saved)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        p.edit().putLong("last", System.currentTimeMillis()).apply()
        if (next >= saved) clear(context)
        return true
    }

    fun active(context: Context): Boolean {
        if (!enabled(context)) return false
        val saved = prefs(context).getInt("saved", -1)
        if (saved < 0 || MicLive.recording(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(AudioManager.STREAM_MUSIC) < saved
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Mic-herstel uit"
        active(context) -> "Volume komt terug"
        else -> "Mic-herstel aan"
    }

    private fun clear(context: Context) {
        prefs(context).edit().remove("saved").remove("since").apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
