package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.BatteryManager
import java.util.Calendar

/**
 * Tussen 22:00 en 07:00, telefoon aan de lader → muziek max 35%.
 * Beschermt oren bij nachtelijk laden + headset.
 */
object ChargeNightCap {
    private const val PREFS = "sounmax_charge_night"
    private const val CAP_PCT = 35

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) clear(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return h >= 22 || h < 7
    }

    fun charging(context: Context): Boolean {
        val bm = context.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.isCharging
    }

    fun shouldCap(context: Context) = enabled(context) && inWindow() && charging(context)

    fun apply(context: Context): Boolean {
        if (!shouldCap(context)) {
            if (prefs(context).getBoolean("capped", false)) clear(context)
            return false
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cap = (max * CAP_PCT) / 100
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > cap) {
            if (!prefs(context).getBoolean("capped", false)) {
                prefs(context).edit().putInt("prev", cur).putBoolean("capped", true).apply()
            }
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            return true
        }
        prefs(context).edit().putBoolean("capped", true).apply()
        return false
    }

    fun active(context: Context) = shouldCap(context)

    fun label(context: Context): String = when {
        !enabled(context) -> "Nachtladen uit"
        active(context) -> "Nachtladen (cap 35%)"
        else -> "Nachtladen aan"
    }

    private fun clear(context: Context) {
        prefs(context).edit().putBoolean("capped", false).remove("prev").apply()
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
