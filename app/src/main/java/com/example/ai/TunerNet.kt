package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Echte verbinding. Validated = internet werkt, niet alleen radio aan. */
object TunerNet {
    fun validated(context: Context): Boolean {
        val caps = caps(context) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun onWifi(context: Context): Boolean =
        caps(context)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

    fun metered(context: Context): Boolean =
        caps(context)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) != true &&
            validated(context) && !onWifi(context)

    fun label(context: Context): String {
        val caps = caps(context) ?: return "Offline"
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) return "Offline"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobiel"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Online"
        }
    }

    private fun caps(context: Context): NetworkCapabilities? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return null
        return cm.getNetworkCapabilities(network)
    }
}
