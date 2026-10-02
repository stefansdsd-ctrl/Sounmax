package com.example.data

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioManager

/**
 * Vergrendelscherm + muziek boven 72% -> cap 54%.
 * Scherm kan aan staan (lock/AOD) terwijl de telefoon in de zak zit.
 */
object LockSoft {
    private const val PREFS = "sounmax_lock_soft"
    private const val CAP_PCT = 54
    private const val TRIGGER_PCT = 72

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun locked(context: Context): Boolean = try {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        km.isKeyguardLocked
    } catch (_: Exception) {
        false
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !locked(context)) return false
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && locked(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Slot-cap uit"
        locked(context) -> "Slot-cap (54%)"
        else -> "Slot-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
