package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Regen of harde wind (Open-Meteo): boven 70% → 50%, in stapjes.
 * Alleen bij echt gevalideerd internet. Geen aanname dat Wi-Fi online is.
 */
object WeatherSoft {
    private const val PREFS = "sounmax_weather_soft"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 70
    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun badWeather(context: Context): Boolean {
        refreshIfDue(context)
        return prefs(context).getBoolean("bad", false)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        refreshIfDue(context)
        if (!prefs(context).getBoolean("bad", false)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last_apply", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && badWeather(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Weer-cap uit"
        badWeather(context) -> "Weer-cap (50%)"
        else -> "Weer-cap aan"
    }

    private fun refreshIfDue(context: Context) {
        val p = prefs(context)
        val now = System.currentTimeMillis()
        if (now - p.getLong("fetched", 0L) < 20 * 60_000) return
        if (!validatedInternet(context)) return
        val loc = lastFix(context) ?: return
        p.edit().putLong("fetched", now).apply()
        Thread {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=${loc.first}&longitude=${loc.second}&current=precipitation,wind_speed_10m,weather_code"
                val req = Request.Builder().url(url).build()
                client.newCall(req).execute().use { res ->
                    if (!res.isSuccessful) return@use
                    val cur = JSONObject(res.body?.string() ?: return@use).optJSONObject("current") ?: return@use
                    val rain = cur.optDouble("precipitation", 0.0)
                    val wind = cur.optDouble("wind_speed_10m", 0.0)
                    val code = cur.optInt("weather_code", 0)
                    val bad = rain >= 0.3 || wind >= 28.0 || code in 51..99
                    prefs(context).edit().putBoolean("bad", bad).putInt("code", code).apply()
                }
            } catch (_: Exception) {
            }
        }.start()
    }

    private fun validatedInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun lastFix(context: Context): Pair<Double, Double>? {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
        val loc = providers.mapNotNull { p ->
            try { lm.getLastKnownLocation(p) } catch (_: Exception) { null }
        }.maxByOrNull { it.time } ?: return null
        return loc.latitude to loc.longitude
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
