package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Echte verbinding. Validated = internet werkt, niet alleen radio aan. */
object TunerNet {
    private const val SLOW_KBPS = 150

    fun validated(context: Context): Boolean {
        val caps = caps(context) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
            !portal(context)
    }

    fun onWifi(context: Context): Boolean =
        caps(context)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

    /** Login-pagina (hotel, trein). Cloud kan hier niet bij. */
    fun portal(context: Context): Boolean =
        caps(context)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) == true

    /**
     * Downstream onder 150 kbps. 0 = onbekend, telt niet als traag.
     * Alleen als het netwerk wél validated is.
     */
    fun slow(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        if (portal(context)) return false
        val kbps = caps.linkDownstreamBandwidthKbps
        return kbps in 1 until SLOW_KBPS
    }

    /** Mobiel in het buitenland. Wi-Fi telt niet als roaming. */
    fun roaming(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        if (portal(context)) return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
    }

    /** Actieve VPN. Blokkeert Gemini niet; alleen een label. */
    fun vpn(context: Context): Boolean =
        caps(context)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

    /** Actief netwerk is metered (mobiel of metered hotspot). Offline telt niet. */
    fun metered(context: Context): Boolean {
        if (!validated(context)) return false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.isActiveNetworkMetered
    }

    fun label(context: Context): String {
        val caps = caps(context) ?: return "Offline"
        if (portal(context)) return "${transport(caps)} · portal"
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) return "Offline"
        val base = transport(caps)
        if (roaming(context)) return "$base · roaming"
        if (slow(context)) return "$base · traag"
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.isActiveNetworkMetered) return "$base · data"
        if (vpn(context)) return "$base · vpn"
        return base
    }

    private fun transport(caps: NetworkCapabilities): String = when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobiel"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
        else -> "Online"
    }

    private fun caps(context: Context): NetworkCapabilities? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return null
        return cm.getNetworkCapabilities(network)
    }
}
