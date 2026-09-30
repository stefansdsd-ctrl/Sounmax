package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Echte link-status via ConnectivityManager.
 * Offline-modus slaat Gemini over en gebruikt lokale fallback.
 */
object OfflineGuard {
    private const val PREFS = "sounmax_offline_guard"
    private const val KEY_FORCE = "force_offline"

    data class Snap(
        val internet: Boolean,
        val wifi: Boolean,
        val cellular: Boolean,
        val forced: Boolean
    ) {
        val usable get() = internet && !forced
        val transport: String = when {
            forced -> "offline-modus"
            wifi -> "wifi"
            cellular -> "mobiel"
            internet -> "net"
            else -> "geen net"
        }
    }

    fun snap(context: Context): Snap {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }
        val internet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val wifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val cell = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        return Snap(internet, wifi, cell, forced(context))
    }

    fun forced(context: Context): Boolean =
        prefs(context).getBoolean(KEY_FORCE, false)

    fun setForced(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_FORCE, on).apply()
    }

    fun cycle(context: Context): String {
        setForced(context, !forced(context))
        return label(context)
    }

    fun shouldSkipCloud(context: Context): Boolean {
        val s = snap(context)
        return !s.usable
    }

    fun label(context: Context): String {
        val s = snap(context)
        return when {
            s.forced -> "Net: offline-modus"
            !s.internet -> "Net: offline"
            else -> "Net: ${s.transport}"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
