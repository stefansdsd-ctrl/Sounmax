package com.example.media

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HeadsetPlace(
    val name: String,
    val lat: Double,
    val lng: Double,
    val savedAtMs: Long,
    val rssiDbm: Int?
) {
    fun ageLabel(): String {
        val min = ((System.currentTimeMillis() - savedAtMs) / 60_000L).coerceAtLeast(0)
        return when {
            min < 2 -> "net"
            min < 60 -> "${min} min geleden"
            min < 24 * 60 -> "${min / 60} u geleden"
            else -> "${min / (24 * 60)} d geleden"
        }
    }

    fun proximityLabel(): String {
        val rssi = rssiDbm ?: return "signaal onbekend"
        return when {
            rssi >= -55 -> "zeer dichtbij"
            rssi >= -70 -> "in de buurt"
            rssi >= -85 -> "verder weg"
            else -> "zwak signaal"
        }
    }
}

object HeadsetLocator {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_LAT = "headset_last_lat"
    private const val KEY_LNG = "headset_last_lng"
    private const val KEY_AT = "headset_last_at"
    private const val KEY_NAME = "headset_last_name"
    private const val KEY_RSSI = "headset_last_rssi"

    private val _place = MutableStateFlow<HeadsetPlace?>(null)
    val place: StateFlow<HeadsetPlace?> = _place.asStateFlow()

    fun load(context: Context): HeadsetPlace? {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!p.contains(KEY_LAT) || !p.contains(KEY_LNG)) {
            _place.value = null
            return null
        }
        val found = HeadsetPlace(
            name = p.getString(KEY_NAME, "Headset") ?: "Headset",
            lat = p.getFloat(KEY_LAT, 0f).toDouble(),
            lng = p.getFloat(KEY_LNG, 0f).toDouble(),
            savedAtMs = p.getLong(KEY_AT, 0L),
            rssiDbm = p.getInt(KEY_RSSI, Int.MIN_VALUE).takeIf { it in -120..0 }
        )
        _place.value = found
        return found
    }

    fun rememberIfConnected(context: Context, status: HeadsetStatus) {
        if (!status.connected) {
            load(context)
            return
        }
        val loc = lastLocation(context) ?: return
        val name = status.name ?: "Headset"
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putFloat(KEY_LAT, loc.first.toFloat())
            .putFloat(KEY_LNG, loc.second.toFloat())
            .putLong(KEY_AT, System.currentTimeMillis())
            .putString(KEY_NAME, name)
            .putInt(KEY_RSSI, status.rssiDbm ?: Int.MIN_VALUE)
            .apply()
        _place.value = HeadsetPlace(name, loc.first, loc.second, System.currentTimeMillis(), status.rssiDbm)
    }

    fun openMaps(context: Context) {
        val spot = _place.value ?: load(context) ?: return
        val uri = Uri.parse("geo:${spot.lat},${spot.lng}?q=${spot.lat},${spot.lng}(${Uri.encode(spot.name)})")
        val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val web = Uri.parse("https://maps.google.com/?q=${spot.lat},${spot.lng}")
            context.startActivity(Intent(Intent.ACTION_VIEW, web).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun lastLocation(context: Context): Pair<Double, Double>? {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val loc = Tasks.await(client.lastLocation, 2, TimeUnit.SECONDS) ?: return null
            loc.latitude to loc.longitude
        } catch (_: Exception) {
            null
        }
    }
}
