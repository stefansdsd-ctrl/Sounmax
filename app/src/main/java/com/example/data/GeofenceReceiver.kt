package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        val ids = event.triggeringGeofences?.map { it.requestId } ?: return

        val profiles = GeofenceProfiles(context)
        val zones = profiles.loadZones()

        for (id in ids) {
            val zone = zones.find { it.id == id } ?: continue
            when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    // Apply zone EQ slot + ANC
                    // EqPersist / RealAncController integratie hier
                    android.util.Log.d("Geofence", "ENTER $id eq=${zone.eqSlot} anc=${zone.ancOn}")
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    android.util.Log.d("Geofence", "EXIT $id → default")
                }
            }
        }
    }
}
