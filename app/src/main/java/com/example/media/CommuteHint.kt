package com.example.media

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Kies onderweg-scene op wifi-SSID / activiteit. */
object CommuteHint {
    private val TRAIN = listOf("ns", "trein", "thalys", "ice", "ov-chip", "wifi-in-de-trein")
    private val METRO = listOf("gvb", "metro", "ret", "htm")
    private val CAFE = listOf("starbucks", "costa", "cafe", "koffie")

    fun suggestedId(context: Context, activity: String? = null): String? {
        val act = activity?.lowercase().orEmpty()
        when {
            "walk" in act || "on_foot" in act -> return "walk"
            "bike" in act || "cycling" in act -> return "bike"
            "vehicle" in act || "in_vehicle" in act -> return "car"
        }
        val ssid = currentSsid(context)?.lowercase() ?: return null
        return when {
            TRAIN.any { it in ssid } -> "train"
            METRO.any { it in ssid } -> "metro"
            CAFE.any { it in ssid } -> "cafe"
            else -> null
        }
    }

    fun suggestedScene(context: Context, activity: String? = null): ListeningScene? =
        SceneLookup.byId(suggestedId(context, activity))

    private fun currentSsid(context: Context): String? {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nw = cm.activeNetwork ?: return null
            val caps = cm.getNetworkCapabilities(nw) ?: return null
            if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return null
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            wm.connectionInfo?.ssid?.trim('"')?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
        } catch (_: Exception) {
            null
        }
    }
}
