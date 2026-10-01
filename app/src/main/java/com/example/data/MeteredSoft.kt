package com.example.data

import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Mobiel of metered netwerk (geen wifi) + muziek boven 70% → cap 52%.
 * Echte ConnectivityManager-check, los van klok-caps.
 */
object MeteredSoft {
    private const val PREFS = "sounmax_metered_soft"
    private const val CAP_PCT = 52
    private const val TRIGGER_PCT = 70

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun onMetered(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return false
        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return false
        val cell = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val metered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        return cell || metered
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !onMetered(context)) return false
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

    fun active(context: Context) = enabled(context) && onMetered(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Mobiel-cap uit"
        onMetered(context) -> "Mobiel-cap (52%)"
        else -> "Mobiel-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
