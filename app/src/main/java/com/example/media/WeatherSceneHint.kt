package com.example.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Open-Meteo (geen API-key). Kiest regen/wind/hitte-scene op basis van huidige weer.
 */
object WeatherSceneHint {
    data class Hint(val label: String, val scene: ListeningScene)

    private val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()

    @Volatile private var cached: Hint? = null
    @Volatile private var cachedAt = 0L
    private const val TTL_MS = 20 * 60_000L

    fun cachedHint(): Hint? {
        val age = System.currentTimeMillis() - cachedAt
        return if (age in 0 until TTL_MS) cached else null
    }

    fun refresh(context: Context): Hint? {
        cachedHint()?.let { return it }
        return try {
            val (lat, lon) = coords(context)
            val url =
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                    "&current=precipitation,rain,wind_speed_10m,temperature_2m" +
                    "&timezone=auto"
            val body = client.newCall(Request.Builder().url(url).build()).execute().use { resp ->
                if (!resp.isSuccessful) return null
                resp.body?.string() ?: return null
            }
            val cur = JSONObject(body).optJSONObject("current") ?: return null
            val rain = cur.optDouble("rain", 0.0) + cur.optDouble("precipitation", 0.0)
            val wind = cur.optDouble("wind_speed_10m", 0.0)
            val temp = cur.optDouble("temperature_2m", 18.0)
            val hint = when {
                rain >= 0.4 -> scene("commute_rain", "☔ Regen")
                    ?: scene("rainwalk", "☔ Regen")
                wind >= 8.0 -> scene("wind", "💨 Wind")
                temp >= 28.0 -> scene("garden", "☀️ Warm")
                else -> null
            }
            cached = hint
            cachedAt = System.currentTimeMillis()
            hint
        } catch (_: Exception) {
            null
        }
    }

    private fun scene(id: String, label: String): Hint? =
        SceneLookup.byId(id)?.let { Hint(label, it) }

    private fun coords(context: Context): Pair<Double, Double> {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val loc = lm.getProviders(true).firstNotNullOfOrNull { p ->
                runCatching { lm.getLastKnownLocation(p) }.getOrNull()
            }
            if (loc != null) return loc.latitude to loc.longitude
        }
        return 52.37 to 4.89
    }
}
