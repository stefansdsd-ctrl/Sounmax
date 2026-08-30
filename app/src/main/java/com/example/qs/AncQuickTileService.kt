package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.AncMode

class AncQuickTileService : TileService() {
    override fun onStartListening() {
        val name = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            .getString("last_anc", AncMode.STRONG.name)
        val mode = runCatching { AncMode.valueOf(name ?: AncMode.STRONG.name) }.getOrDefault(AncMode.STRONG)
        updateTile(mode)
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val current = runCatching {
            AncMode.valueOf(prefs.getString("last_anc", AncMode.STRONG.name) ?: AncMode.STRONG.name)
        }.getOrDefault(AncMode.STRONG)
        val modes = AncMode.values()
        val next = modes[(modes.indexOf(current) + 1) % modes.size]
        prefs.edit().putString("last_anc", next.name).apply()
        sendBroadcast(
            Intent(ACTION_CYCLE_ANC).setPackage(packageName).putExtra("anc", next.name)
        )
        updateTile(next)
    }

    private fun updateTile(mode: AncMode) {
        qsTile?.apply {
            state = if (mode == AncMode.OFF) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
            label = mode.displayName
            subtitle = "ANC"
            updateTile()
        }
    }

    companion object {
        const val ACTION_CYCLE_ANC = "com.example.action.CYCLE_ANC"
    }
}
