package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.BluetoothCodec

/** Waarschuwt als LDAC + lage accu of zwak RSSI. Rate-limit 90s. */
object LdacWarn {
    private const val PREFS = "sounmax_ldac_warn"
    private const val KEY_TS = "last_ts"
    private const val COOLDOWN_MS = 90_000L
    const val MESSAGE = "bitrate daalt — dichterbij of AAC"

    fun shouldWarn(
        codec: BluetoothCodec?,
        batteryPercent: Int?,
        rssi: Int?
    ): Boolean {
        if (codec != BluetoothCodec.LDAC) return false
        val lowBattery = (batteryPercent ?: 100) < 20
        val weakRssi = rssi != null && rssi < -75
        return lowBattery || weakRssi
    }

    fun maybeToast(
        context: Context,
        codec: BluetoothCodec?,
        batteryPercent: Int?,
        rssi: Int?
    ): Boolean {
        if (!shouldWarn(codec, batteryPercent, rssi)) return false
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(KEY_TS, 0L) < COOLDOWN_MS) return false
        prefs.edit().putLong(KEY_TS, now).apply()
        Toast.makeText(context, MESSAGE, Toast.LENGTH_LONG).show()
        return true
    }
}
