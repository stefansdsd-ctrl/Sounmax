package com.example.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

data class WeatherSnapshot(
    val temperatureC: Double,
    val precipitationMm: Double,
    val windKmh: Double,
    val weatherCode: Int,
    val outdoor: Boolean
) {
    val raining: Boolean
        get() = precipitationMm >= 0.2 || weatherCode in RAIN_CODES
    val windy: Boolean
        get() = windKmh >= 28.0
    val hot: Boolean
        get() = temperatureC >= 28.0
    val cold: Boolean
        get() = temperatureC <= 2.0

    fun sceneOverride(): ListeningScene? = when {
        raining -> ListeningScenes.byId("rain")
        outdoor && windy -> ListeningScenes.byId("hike")
        outdoor && hot -> ListeningScenes.byId("walk")
        outdoor && cold -> ListeningScenes.byId("commute")
        outdoor -> ListeningScenes.byId("walk")
        else -> null
    }

    fun label(): String {
        val sky = when {
            raining -> "regen"
            weatherCode in 1..3 -> "bewolkt"
            else -> "helder"
        }
        val where = if (outdoor) "buiten" else "binnen"
        return "${temperatureC.toInt()}° · $sky · ${windKmh.toInt()} km/u · $where"
    }

    companion object {
        val RAIN_CODES = (51..67) + (80..99) + (71..77)
    }
}

object WeatherAdvisor {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ENABLED = "weather_scene"
    const val KEY_LABEL = "weather_label"
    const val KEY_LAT = "weather_lat"
    const val KEY_LNG = "weather_lng"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun lastLabel(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LABEL, null)

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun refreshBlocking(context: Context): WeatherSnapshot? {
        if (!enabled(context)) return null
        val loc = lastLocation(context) ?: return cachedCoords(context)?.let { fetch(it.first, it.second, outdoor = false) }
        val outdoor = loc.accuracy in 1f..80f && (loc.speed <= 0f || loc.hasSpeed())
        val snap = fetch(loc.latitude, loc.longitude, outdoor) ?: return null
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_LABEL, snap.label())
            .putFloat(KEY_LAT, loc.latitude.toFloat())
            .putFloat(KEY_LNG, loc.longitude.toFloat())
            .apply()
        return snap
    }

    fun suggest(context: Context, fallback: ListeningScene): ListeningScene {
        val snap = runCatching { refreshBlocking(context) }.getOrNull() ?: return fallback
        return snap.sceneOverride() ?: fallback
    }

    private fun cachedCoords(context: Context): Pair<Double, Double>? {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!p.contains(KEY_LAT)) return null
        return p.getFloat(KEY_LAT, 0f).toDouble() to p.getFloat(KEY_LNG, 0f).toDouble()
    }

    private fun lastLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val last = Tasks.await(client.lastLocation, 3, TimeUnit.SECONDS)
            last ?: Tasks.await(
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null),
                6,
                TimeUnit.SECONDS
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun fetch(lat: Double, lng: Double, outdoor: Boolean): WeatherSnapshot? {
        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng" +
                "&current=temperature_2m,precipitation,weather_code,wind_speed_10m&wind_speed_unit=kmh"
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 4000
            readTimeout = 4000
            requestMethod = "GET"
        }
        return try {
            if (conn.responseCode != 200) return null
            val body = conn.inputStream.bufferedReader().readText()
            val cur = JSONObject(body).optJSONObject("current") ?: return null
            WeatherSnapshot(
                temperatureC = cur.optDouble("temperature_2m", Double.NaN),
                precipitationMm = cur.optDouble("precipitation", 0.0),
                windKmh = cur.optDouble("wind_speed_10m", 0.0),
                weatherCode = cur.optInt("weather_code", 0),
                outdoor = outdoor
            ).takeIf { !it.temperatureC.isNaN() }
        } catch (_: Exception) {
            null
        } finally {
            conn.disconnect()
        }
    }
}
