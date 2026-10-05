package com.example.data

import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager

/**
 * Onthoudt muziekvolume per Wi-Fi-SSID en zet het stapsgewijs terug
 * alleen als de headset (A2DP) actief is. Leert pas na een expliciete save.
 */
object WifiVolumeMemory {
    private const val PREFS = "sounmax_wifi_volume"
    private const val STEP = 2

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) remember(context)
        return label(context)
    }

    fun remember(context: Context): Boolean {
        val id = ssid(context) ?: return false
        val pct = musicPct(context)
        prefs(context).edit()
            .putInt(key(id), pct)
            .putString("last_ssid", id)
            .apply()
        return true
    }

    fun ssid(context: Context): String? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return null
        val caps = cm.getNetworkCapabilities(network) ?: return null
        val wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (!wifi) return null
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val raw = wm.connectionInfo?.ssid?.trim('"') ?: return null
        if (raw.isBlank() || raw.equals("<unknown ssid>", ignoreCase = true)) return null
        return raw.take(32)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val id = ssid(context) ?: return false
        val target = prefs(context).getInt(key(id), -1)
        if (target < 0) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isBluetoothA2dpOn || !am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val want = ((max * target) / 100).coerceIn(0, max)
        if (kotlin.math.abs(cur - want) <= 1) return false
        val next = if (cur > want) (cur - STEP).coerceAtLeast(want) else (cur + STEP).coerceAtMost(want)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        prefs(context).edit()
            .putLong("last", System.currentTimeMillis())
            .putString("last_ssid", id)
            .apply()
        return true
    }

    fun active(context: Context): Boolean {
        if (!enabled(context)) return false
        val id = ssid(context) ?: return false
        return prefs(context).getInt(key(id), -1) >= 0
    }

    fun label(context: Context): String {
        if (!enabled(context)) return "Wi-Fi-volume uit"
        val id = ssid(context)
        if (id == null) return "Wi-Fi-volume aan (geen netwerk)"
        val saved = prefs(context).getInt(key(id), -1)
        return if (saved < 0) "Wi-Fi-volume: $id nog niet opgeslagen" else "Wi-Fi-volume: $id $saved%"
    }

    private fun musicPct(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return (100 * am.getStreamVolume(AudioManager.STREAM_MUSIC)) / max
    }

    private fun key(ssid: String) = "v_" + ssid.lowercase()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
