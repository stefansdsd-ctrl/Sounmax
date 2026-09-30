package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Software bel-transparantie: MODE_IN_COMMUNICATION + lagere muziek.
 * Hardware ANC-transparantie volgt na GATT-dump.
 */
object CallTransparency {
    private const val PREFS = "sounmax_call_transparency"
    private const val KEY_ON = "on"
    private const val KEY_SAVED_MODE = "saved_mode"
    private const val KEY_SAVED_VOL = "saved_vol"

    fun isOn(context: Context) = prefs(context).getBoolean(KEY_ON, false)

    fun cycle(context: Context): String {
        return if (isOn(context)) {
            stop(context)
            label(context)
        } else {
            start(context)
            label(context)
        }
    }

    fun start(context: Context) {
        val am = audio(context)
        prefs(context).edit()
            .putBoolean(KEY_ON, true)
            .putInt(KEY_SAVED_MODE, am.mode)
            .putInt(KEY_SAVED_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .apply()
        runCatching { am.mode = AudioManager.MODE_IN_COMMUNICATION }
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (max * 35) / 100, 0)
        if (!CallHold.active(context)) CallHold.start(context, 10)
    }

    fun stop(context: Context) {
        val am = audio(context)
        val savedMode = prefs(context).getInt(KEY_SAVED_MODE, AudioManager.MODE_NORMAL)
        val savedVol = prefs(context).getInt(KEY_SAVED_VOL, -1)
        prefs(context).edit().putBoolean(KEY_ON, false).apply()
        runCatching { am.mode = savedMode }
        if (savedVol >= 0) {
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, savedVol.coerceIn(0, max), 0)
        }
    }

    fun apply(context: Context): Boolean {
        if (!isOn(context)) return false
        val am = audio(context)
        if (am.mode != AudioManager.MODE_IN_COMMUNICATION) {
            runCatching { am.mode = AudioManager.MODE_IN_COMMUNICATION }
        }
        return true
    }

    fun label(context: Context): String =
        if (isOn(context)) "Bel-transparantie aan" else "Bel-transparantie uit"

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
