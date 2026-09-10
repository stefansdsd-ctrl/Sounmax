package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.ble.RealAncController
import com.example.dsp.AncMode

/**
 * Zet ANC uit onder 15% headset-accu om speeltijd te redden.
 * Herstelt de vorige modus boven 25%. Rate-limit 3 min.
 */
object LowBatteryAncSaver {
    private const val PREFS = "sounmax_anc_saver"
    private const val KEY_SAVED = "saved_anc"
    private const val KEY_ACTIVE = "saver_on"
    private const val KEY_TS = "last_ts"
    private const val COOLDOWN_MS = 180_000L
    const val OFF_THRESHOLD = 15
    const val RESTORE_THRESHOLD = 25

    fun tick(context: Context, batteryPercent: Int?) {
        if (batteryPercent == null || batteryPercent !in 0..100) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(KEY_TS, 0L) < COOLDOWN_MS) return

        val currentName = wellness.getString("last_anc", AncMode.STRONG.name) ?: AncMode.STRONG.name
        val current = runCatching { AncMode.valueOf(currentName) }.getOrDefault(AncMode.STRONG)
        val saverOn = prefs.getBoolean(KEY_ACTIVE, false)

        if (batteryPercent < OFF_THRESHOLD && current != AncMode.OFF) {
            prefs.edit()
                .putString(KEY_SAVED, current.name)
                .putBoolean(KEY_ACTIVE, true)
                .putLong(KEY_TS, now)
                .apply()
            wellness.edit().putString("last_anc", AncMode.OFF.name).apply()
            RealAncController.apply(context, AncMode.OFF)
            Toast.makeText(context, "ANC uit — accu $batteryPercent%", Toast.LENGTH_SHORT).show()
        } else if (saverOn && batteryPercent >= RESTORE_THRESHOLD) {
            val restoreName = prefs.getString(KEY_SAVED, AncMode.STRONG.name) ?: AncMode.STRONG.name
            val restore = runCatching { AncMode.valueOf(restoreName) }.getOrDefault(AncMode.STRONG)
            prefs.edit().putBoolean(KEY_ACTIVE, false).putLong(KEY_TS, now).apply()
            wellness.edit().putString("last_anc", restore.name).apply()
            RealAncController.apply(context, restore)
            Toast.makeText(context, "ANC terug: ${restore.displayName}", Toast.LENGTH_SHORT).show()
        }
    }
}
