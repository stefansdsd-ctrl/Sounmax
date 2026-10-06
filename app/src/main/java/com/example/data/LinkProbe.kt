package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection
import java.net.URL

/**
 * Echte verbindingstest: validated internet + latency naar generate_204.
 * Geen fake status. Draait alleen op aanroep, niet op de main thread.
 */
object LinkProbe {
    private const val PREFS = "sounmax_link_probe"
    private const val URL_204 = "https://connectivitycheck.gstatic.com/generate_204"

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        return label(context)
    }

    fun snapshot(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }
        val validated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        val wifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val cell = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        val kind = when {
            wifi -> "wifi"
            cell -> "mobiel"
            caps != null -> "ander"
            else -> "geen"
        }
        if (!validated) {
            save(context, kind, -1, false)
            return "Link: $kind, niet gevalideerd"
        }
        val ms = ping()
        save(context, kind, ms, ms in 0..1499)
        return if (ms < 0) "Link: $kind, timeout" else "Link: $kind ${ms}ms"
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val last = prefs(context).getLong("last", 0L)
        if (System.currentTimeMillis() - last < 60_000L) return false
        snapshot(context)
        return true
    }

    fun label(context: Context): String {
        if (!enabled(context)) return "Linkmeting uit"
        val kind = prefs(context).getString("kind", "") ?: ""
        val ms = prefs(context).getInt("ms", -1)
        if (kind.isBlank()) return "Linkmeting aan"
        return if (ms < 0) "Link: $kind, geen antwoord" else "Link: $kind ${ms}ms"
    }

    private fun ping(): Int {
        val start = System.nanoTime()
        return try {
            val conn = (URL(URL_204).openConnection() as HttpURLConnection).apply {
                requestMethod = "HEAD"
                connectTimeout = 2500
                readTimeout = 2500
                instanceFollowRedirects = false
                useCaches = false
            }
            val code = conn.responseCode
            conn.disconnect()
            if (code != 204 && code != 200) -1
            else ((System.nanoTime() - start) / 1_000_000L).toInt().coerceAtMost(9999)
        } catch (_: Exception) {
            -1
        }
    }

    private fun save(context: Context, kind: String, ms: Int, ok: Boolean) {
        prefs(context).edit()
            .putString("kind", kind)
            .putInt("ms", ms)
            .putBoolean("ok", ok)
            .putLong("last", System.currentTimeMillis())
            .apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
