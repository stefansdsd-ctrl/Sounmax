package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.BluetoothCodec
import com.example.widget.SoundMaxWidget

/** Snackbar/toast als LDAC + lage accu of zwak RSSI. */
object LdacWarn {
    const val KEY_CODEC = "last_codec_label"
    const val KEY_KBPS = "last_codec_kbps"
    private const val KEY_LAST_MS = "ldac_warn_ms"

    fun remember(context: Context, label: String?, kbps: Int?) {
        val p = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE).edit()
        if (!label.isNullOrBlank()) p.putString(KEY_CODEC, label)
        if (kbps != null && kbps > 0) p.putInt(KEY_KBPS, kbps)
        p.apply()
    }

    fun maybeWarn(context: Context, codec: BluetoothCodec?, batteryPct: Int, rssiDbm: Int) {
        if (codec != BluetoothCodec.LDAC && codec?.name?.contains("LDAC") != true) {
            val label = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CODEC, "") ?: ""
            if (!label.uppercase().contains("LDAC")) return
        }
        val weak = rssiDbm != 0 && rssiDbm <= -75
        val lowBat = batteryPct in 0..19
        if (!weak && !lowBat) return
        val p = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - p.getLong(KEY_LAST_MS, 0L) < 8 * 60_000L) return
        p.edit().putLong(KEY_LAST_MS, now).apply()
        Toast.makeText(
            context.applicationContext,
            "bitrate daalt — dichterbij of AAC",
            Toast.LENGTH_LONG
        ).show()
    }

    fun statusLine(context: Context): String {
        val p = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val anc = p.getString("last_anc", "ADAPTIVE") ?: "ADAPTIVE"
        val bat = p.getInt(SoundMaxWidget.KEY_BATTERY, -1)
        val codec = p.getString(KEY_CODEC, null)
        val kbps = p.getInt(KEY_KBPS, 0)
        val eq = com.example.dsp.EqSnapshot.activeSide(context)
        val parts = buildList {
            add(anc.replace('_', ' '))
            if (bat in 0..100) add("$bat%")
            if (!codec.isNullOrBlank()) {
                add(if (kbps > 0) "$codec $kbps" else codec)
            }
            add("EQ $eq")
        }
        return parts.joinToString(" · ")
    }
}
