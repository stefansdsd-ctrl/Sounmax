package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Plotse volumestijging (≥3 stappen binnen 8 s) wordt teruggedraaid naar vorige+1.
 * Beschermt tegen een per-ongeluk volume-rocker. Daling (duck) wordt niet aangeraakt.
 */
object JumpGuard {
    private const val PREFS = "sounmax_jump_guard"
    private const val WINDOW_MS = 8_000L
    private const val MIN_STEPS = 3

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).putBoolean("caught", false).apply()
        return label(context)
    }

    fun apply(context: Context): Boolean {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val p = prefs(context)
        val prev = p.getInt("prev", cur)
        val at = p.getLong("at", 0L)
        val now = System.currentTimeMillis()
        if (!enabled(context)) {
            p.edit().putInt("prev", cur).putLong("at", now).putBoolean("caught", false).apply()
            return false
        }
        val mode = am.mode
        val ducked = mode == AudioManager.MODE_RINGTONE ||
            mode == AudioManager.MODE_IN_CALL ||
            mode == AudioManager.MODE_IN_COMMUNICATION
        val jumped = !ducked && now - at in 1..WINDOW_MS && cur - prev >= MIN_STEPS
        if (jumped) {
            val back = (prev + 1).coerceIn(0, max)
            if (cur > back) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, back, 0)
                p.edit().putInt("prev", back).putLong("at", now).putBoolean("caught", true).apply()
                return true
            }
        }
        p.edit().putInt("prev", cur).putLong("at", now).putBoolean("caught", false).apply()
        return false
    }

    fun active(context: Context) = enabled(context) && prefs(context).getBoolean("caught", false)

    fun label(context: Context) = when {
        !enabled(context) -> "Sprong-cap uit"
        active(context) -> "Sprong-cap (terug)"
        else -> "Sprong-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
