package com.example.data

import android.content.Context
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.provider.Settings

/**
 * Hotspot aan + muziek boven 70% -> cap 50%.
 * Telefoon ligt dan vaak open op tafel.
 */
object HotspotSoft {
    private const val PREFS = "sounmax_hotspot_soft"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 70

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun hotspotOn(context: Context): Boolean {
        try {
            val state = Settings.Global.getInt(context.contentResolver, "wifi_ap_state", 0)
            if (state == 13 || state == 3) return true
        } catch (_: Exception) {
        }
        return try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = wm.javaClass.getMethod("isWifiApEnabled")
            method.invoke(wm) as? Boolean == true
        } catch (_: Exception) {
            false
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !hotspotOn(context)) return false
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

    fun active(context: Context) = enabled(context) && hotspotOn(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Hotspot-cap uit"
        hotspotOn(context) -> "Hotspot-cap (50%)"
        else -> "Hotspot-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
