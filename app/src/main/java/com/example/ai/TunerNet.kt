package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Echte verbinding. Validated = internet werkt, niet alleen radio aan.
 * Verbeterd met shouldAllowCloud, qualityScore en bandwidthInfo. */
object TunerNet {
    private const val SLOW_KBPS = 150

    data class CloudDecision(val allow: Boolean, val reason: String)
    data class Bandwidth(val downKbps: Int, val upKbps: Int)

    /** Centrale check of cloud (Gemini) toegestaan is. */
    fun shouldAllowCloud(context: Context, force: Boolean = false): CloudDecision {
        if (portal(context)) return CloudDecision(false, "portal")
        if (suspended(context)) return CloudDecision(false, "netwerk pauze")
        if (!validated(context)) return CloudDecision(false, "offline")
        if (force) return CloudDecision(true, "geforceerd")

        if (roaming(context)) return CloudDecision(false, "roaming")
        if (congested(context)) return CloudDecision(false, "druk net")
        if (dataSaver(context)) return CloudDecision(false, "dataspaar")
        if (slow(context)) return CloudDecision(false, "traag net")
        if (metered(context)) return CloudDecision(false, "mobiel data")
        return CloudDecision(true, "ok")
    }

    fun validated(context: Context): Boolean {
        val caps = caps(context) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
            !portal(context)
    }

    fun onWifi(context: Context): Boolean =
        caps(context)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

    fun portal(context: Context): Boolean =
        caps(context)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) == true

    fun slow(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        if (portal(context)) return false
        val kbps = caps.linkDownstreamBandwidthKbps
        return kbps in 1 until SLOW_KBPS
    }

    fun roaming(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        if (portal(context)) return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
    }

    fun vpn(context: Context): Boolean =
        caps(context)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

    fun suspended(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return false
        if (portal(context)) return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
    }

    fun congested(context: Context): Boolean {
        val caps = caps(context) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        if (portal(context) || suspended(context)) return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)
    }

    fun dataSaver(context: Context): Boolean {
        if (!validated(context)) return false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
    }

    fun tempUnmetered(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
        val caps = caps(context) ?: return false
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED)
    }

    fun metered(context: Context): Boolean {
        if (!validated(context) || tempUnmetered(context)) return false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.isActiveNetworkMetered
    }

    /** Downstream + upstream kbps. 0 = onbekend. */
    fun bandwidthInfo(context: Context): Bandwidth {
        val caps = caps(context) ?: return Bandwidth(0, 0)
        return Bandwidth(caps.linkDownstreamBandwidthKbps, caps.linkUpstreamBandwidthKbps)
    }

    /** 0-100 netwerkkwaliteit score (hoger = beter voor cloud). */
    fun qualityScore(context: Context): Int {
        if (!validated(context)) return 0
        var score = 50
        val bw = bandwidthInfo(context)
        score += when {
            bw.downKbps >= 5000 -> 40
            bw.downKbps >= 1000 -> 25
            bw.downKbps >= SLOW_KBPS -> 10
            else -> 0
        }
        if (onWifi(context)) score += 10
        if (tempUnmetered(context)) score += 10
        if (metered(context)) score -= 15
        if (roaming(context) || congested(context) || slow(context)) score -= 20
        if (dataSaver(context)) score -= 10
        return score.coerceIn(0, 100)
    }

    fun label(context: Context): String {
        val decision = shouldAllowCloud(context)
        val base = transport(caps(context) ?: return "Offline")
        val score = qualityScore(context)
        val bw = bandwidthInfo(context)
        val bwStr = if (bw.downKbps > 0) " ${bw.downKbps}kbps" else ""
        return when {
            !decision.allow -> "$base · ${decision.reason}$bwStr (score $score)"
            else -> "$base$bwStr (score $score)"
        }
    }

    fun statusFlow(context: Context): Flow<String> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(label(context)) }
            override fun onLost(network: Network) { trySend(label(context)) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(label(context))
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, callback)
        trySend(label(context))
        awaitClose { cm.unregisterNetworkCallback(callback) }
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
