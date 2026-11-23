package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import kotlin.math.*

/**
 * Location-based profiles: auto-switch EQ/ANC when entering/leaving home, work zones.
 * Uses GeofencingClient + existing location permissions.
 */
class GeofenceProfiles(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)
    private val prefs = context.getSharedPreferences("geofence_profiles", Context.MODE_PRIVATE)

    data class Zone(
        val id: String,
        val lat: Double,
        val lon: Double,
        val radius: Float = 200f,
        val eqSlot: Int = 0,
        val ancOn: Boolean = true
    )

    suspend fun addHomeZone(lat: Double, lon: Double) {
        val zone = Zone("home", lat, lon, 150f, eqSlot = 1, ancOn = true)
        saveZone(zone)
        register(zone)
    }

    suspend fun addWorkZone(lat: Double, lon: Double) {
        val zone = Zone("work", lat, lon, 250f, eqSlot = 2, ancOn = true)
        saveZone(zone)
        register(zone)
    }

    private fun saveZone(z: Zone) {
        prefs.edit()
            .putFloat("${z.id}_lat", z.lat.toFloat())
            .putFloat("${z.id}_lon", z.lon.toFloat())
            .putFloat("${z.id}_radius", z.radius)
            .putInt("${z.id}_eq", z.eqSlot)
            .putBoolean("${z.id}_anc", z.ancOn)
            .apply()
    }

    fun loadZones(): List<Zone> = listOf("home", "work").mapNotNull { id ->
        val lat = prefs.getFloat("${id}_lat", Float.NaN)
        if (lat.isNaN()) null else Zone(
            id,
            lat.toDouble(),
            prefs.getFloat("${id}_lon", 0f).toDouble(),
            prefs.getFloat("${id}_radius", 200f),
            prefs.getInt("${id}_eq", 0),
            prefs.getBoolean("${id}_anc", true)
        )
    }

    private suspend fun register(zone: Zone) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        val geofence = Geofence.Builder()
            .setRequestId(zone.id)
            .setCircularRegion(zone.lat, zone.lon, zone.radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceReceiver::class.java)
        val pending = android.app.PendingIntent.getBroadcast(
            context, 0, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
        )
        client.addGeofences(request, pending).await()
        // client.addGeofences(request, pendingIntent).await()
    }

    fun applyProfile(zoneId: String, entering: Boolean) {
        val z = loadZones().find { it.id == zoneId } ?: return
        if (entering) {
            // Hook into ConnectEqApplier / RealAncController
        }
    }

    companion object {
        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val r = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
            return (2 * r * asin(sqrt(a))).toFloat()
        }
    }
}
