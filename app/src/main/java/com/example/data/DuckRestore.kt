package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Onthoudt het muziekvolume vóór een duck en zet het terug
 * zodra beltoon, gesprek en VoIP voorbij zijn.
 */
object DuckRestore {
    private const val PREFS = "sounmax_duck_restore"

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) clear(context)
        else restore(context)
        return label(context)
    }

    fun remember(context: Context) {
        if (!enabled(context)) return
        val p = prefs(context)
        if (p.getBoolean("pending", false)) return
        val am = audio(context)
        p.edit()
            .putBoolean("pending", true)
            .putInt("vol", am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .putLong("at", System.currentTimeMillis())
            .apply()
    }

    fun restore(context: Context): Boolean {
        if (!enabled(context)) return false
        val p = prefs(context)
        if (!p.getBoolean("pending", false)) return false
        if (stillBusy(context)) return false
        val vol = p.getInt("vol", -1)
        if (vol < 0) {
            clear(context)
            return false
        }
        audio(context).setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
        clear(context)
        return true
    }

    fun active(context: Context) = enabled(context) && prefs(context).getBoolean("pending", false)

    fun label(context: Context) = when {
        !enabled(context) -> "Duck-herstel uit"
        active(context) -> "Duck-herstel wacht"
        else -> "Duck-herstel aan"
    }

    private fun stillBusy(context: Context): Boolean {
        val mode = audio(context).mode
        return mode == AudioManager.MODE_IN_CALL ||
            mode == AudioManager.MODE_IN_COMMUNICATION ||
            mode == AudioManager.MODE_RINGTONE
    }

    private fun clear(context: Context) {
        prefs(context).edit().putBoolean("pending", false).remove("vol").apply()
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
