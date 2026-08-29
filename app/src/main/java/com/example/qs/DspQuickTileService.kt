package com.example.qs

import android.media.audiofx.Equalizer
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class DspQuickTileService : TileService() {
    override fun onStartListening() {
        val on = getSharedPreferences("soundmax_wellness", MODE_PRIVATE).getBoolean("dsp_enabled", true)
        updateTile(on)
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val next = !prefs.getBoolean("dsp_enabled", true)
        prefs.edit().putBoolean("dsp_enabled", next).apply()
        applyHardware(next)
        sendBroadcast(android.content.Intent(ACTION_TOGGLE_DSP).setPackage(packageName).putExtra("enabled", next))
        updateTile(next)
    }

    private fun applyHardware(enabled: Boolean) {
        try {
            Equalizer(0, 0).apply { this.enabled = enabled; release() }
        } catch (_: Exception) {}
    }

    private fun updateTile(on: Boolean) {
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "DSP aan" else "DSP uit"
            subtitle = "SoundMax"
            updateTile()
        }
    }

    companion object {
        const val ACTION_TOGGLE_DSP = "com.example.action.TOGGLE_DSP"
    }
}
