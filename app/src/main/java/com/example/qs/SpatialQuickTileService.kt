package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class SpatialQuickTileService : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        updateTile(prefs.getBoolean("spatializer_on", false), prefs.getBoolean("head_tracking", false))
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val spatial = prefs.getBoolean("spatializer_on", false)
        val track = prefs.getBoolean("head_tracking", false)
        val nextSpatial: Boolean
        val nextTrack: Boolean
        when {
            !spatial -> { nextSpatial = true; nextTrack = false }
            spatial && !track -> { nextSpatial = true; nextTrack = true }
            else -> { nextSpatial = false; nextTrack = false }
        }
        prefs.edit()
            .putBoolean("spatializer_on", nextSpatial)
            .putBoolean("head_tracking", nextTrack)
            .apply()
        sendBroadcast(
            android.content.Intent(ACTION_CYCLE_SPATIAL)
                .setPackage(packageName)
                .putExtra("enabled", nextSpatial)
                .putExtra("head_tracking", nextTrack)
        )
        updateTile(nextSpatial, nextTrack)
    }

    private fun updateTile(on: Boolean, headTracking: Boolean) {
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = when {
                headTracking -> "Head-track"
                on -> "Spatial aan"
                else -> "Spatial uit"
            }
            subtitle = if (headTracking) "yaw → balans" else "SoundMax"
            updateTile()
        }
    }

    companion object {
        const val ACTION_TOGGLE_SPATIAL = "com.example.action.TOGGLE_SPATIAL"
        const val ACTION_CYCLE_SPATIAL = "com.example.action.CYCLE_SPATIAL"
    }
}
