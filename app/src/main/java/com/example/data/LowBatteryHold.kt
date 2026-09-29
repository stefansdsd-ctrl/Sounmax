package com.example.data

import android.content.Context
import android.media.AudioManager
import com.example.dsp.PhoneBattery

/**
 * Volume-cap als de telefoonaccu laag is, zodat BT + DSP langer meegaan.
 * Auto aan ≤ drempel, tenzij de gebruiker hem uitzet tot de accu weer boven de drempel is.
 */
object LowBatteryHold {
    private const val PREFS = "sounmax_lowbatt_hold"
    private const val KEY_DISABLED_UNTIL_CHARGE = "disabled_until_charge"
    private const val KEY_FORCE = "force"
    private const val KEY_CAP = "cap"
    private const val KEY_SAVED_VOL = "saved_vol"
    private const val DEFAULT_THRESHOLD = 15

    fun threshold() = DEFAULT_THRESHOLD

    fun batteryPct(context: Context) = PhoneBattery.percent(context)

    fun active(context: Context): Boolean {
        if (force(context)) return true
        if (prefs(context).getBoolean(KEY_DISABLED_UNTIL_CHARGE, false)) {
            if (batteryPct(context) > threshold()) {
                prefs(context).edit().putBoolean(KEY_DISABLED_UNTIL_CHARGE, false).apply()
            } else {
                return false
            }
        }
        return batteryPct(context) <= threshold()
    }

    fun force(context: Context) = prefs(context).getBoolean(KEY_FORCE, false)

    fun capPercent(context: Context) = prefs(context).getInt(KEY_CAP, 40).coerceIn(20, 70)

    fun apply(context: Context): Boolean {
        if (!active(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val capIdx = (max * capPercent(context)) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > capIdx) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, capIdx, 0)
        }
        if (BatterySaverDsp.enabled(context).not()) {
            BatterySaverDsp.setEnabled(context, true)
        }
        return true
    }

    fun startForce(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        prefs(context).edit()
            .putBoolean(KEY_FORCE, true)
            .putBoolean(KEY_DISABLED_UNTIL_CHARGE, false)
            .putInt(KEY_SAVED_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .putInt(KEY_CAP, 40)
            .apply()
        apply(context)
    }

    fun stop(context: Context) {
        val saved = prefs(context).getInt(KEY_SAVED_VOL, -1)
        prefs(context).edit()
            .putBoolean(KEY_FORCE, false)
            .putBoolean(KEY_DISABLED_UNTIL_CHARGE, true)
            .apply()
        if (saved >= 0) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0)
        }
    }

    fun cycle(context: Context): String {
        return when {
            !active(context) && !prefs(context).getBoolean(KEY_DISABLED_UNTIL_CHARGE, false) -> {
                startForce(context); label(context)
            }
            force(context) -> {
                stop(context); label(context)
            }
            active(context) -> {
                stop(context); label(context)
            }
            else -> {
                prefs(context).edit().putBoolean(KEY_DISABLED_UNTIL_CHARGE, false).apply()
                label(context)
            }
        }
    }

    fun label(context: Context): String {
        val pct = batteryPct(context)
        return when {
            force(context) -> "Accu-hold geforceerd ${capPercent(context)}% ($pct%)"
            prefs(context).getBoolean(KEY_DISABLED_UNTIL_CHARGE, false) && pct <= threshold() ->
                "Accu-hold uit tot opladen ($pct%)"
            active(context) -> "Accu-hold aan $pct%≤${threshold()}% cap ${capPercent(context)}%"
            else -> "Accu-hold uit ($pct%)"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
