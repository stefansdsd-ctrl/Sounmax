package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Echt netwerk, geen aanname.
 * Zonder NET_CAPABILITY_VALIDATED slaat de AI-tuner de cloud over
 * en gebruikt meteen de lokale curve (geen 30s timeout).
 */
object OfflineGuard {
    private const val PREFS = "sounmax_offline_guard"

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        refresh(context)
        return label(context)
    }

    fun hasValidatedInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun onWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /** True = cloud overslaan. */
    fun blockCloud(context: Context): Boolean =
        enabled(context) && !hasValidatedInternet(context)

    fun refresh(context: Context): Boolean {
        val blocked = blockCloud(context)
        prefs(context).edit()
            .putBoolean("blocked", blocked)
            .putLong("last", System.currentTimeMillis())
            .apply()
        return blocked
    }

    fun active(context: Context) = blockCloud(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Net uit"
        !hasValidatedInternet(context) -> "Offline · AI lokaal"
        onWifi(context) -> "Online · Wi-Fi"
        else -> "Online"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
