package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.widget.SoundMaxWidget

class SleepTimerQuickTileService : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val left = SoundMaxWidget.remainingSleepMinutes(prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L))
        updateTile(left)
    }

    override fun onClick() {
        SoundMaxWidget.cycleSleep(this)
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val left = SoundMaxWidget.remainingSleepMinutes(prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L))
        sendBroadcast(
            Intent(ACTION_SLEEP_CHANGED).setPackage(packageName).putExtra("minutes", left)
        )
        updateTile(left)
    }

    private fun updateTile(minutes: Int) {
        qsTile?.apply {
            state = if (minutes > 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (minutes > 0) "Slaap $minutes m" else "Slaaptimer"
            subtitle = "SoundMax"
            updateTile()
        }
    }

    companion object {
        const val ACTION_SLEEP_CHANGED = "com.example.action.SLEEP_CHANGED"
    }
}
