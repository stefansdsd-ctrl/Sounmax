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
        val next = !prefs.getBoolean("spatializer_on", false)
        prefs.edit().putBoolean("spatializer_on", next).apply()
        sendBroadcast(
            android.content.Intent(ACTION_TOGGLE_SPATIAL)
                .setPackage(packageName)
                .putExtra("enabled", next)
        )
        updateTile(next, prefs.getBoolean("head_tracking", false))
    }

    private fun updateTile(on: Boolean, headTracking: Boolean) {
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Spatial aan" else "Spatial uit"
            subtitle = if (headTracking) "Head-track" else "SoundMax"
            updateTile()
        }
    }

    companion object {
        const val ACTION_TOGGLE_SPATIAL = "com.example.action.TOGGLE_SPATIAL"
    }
}
