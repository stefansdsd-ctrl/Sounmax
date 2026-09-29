package com.example.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager

/**
 * Volume-cap + DSP-spaarstand als de telefoon te warm wordt (accu ≥40°C).
 */
object ThermalHold {
    private const val PREFS = "sounmax_thermal_hold"
    private const val KEY_DISABLED = "disabled_until_cool"
    private const val KEY_FORCE = "force"
    private const val KEY_CAP = "cap"
    private const val KEY_SAVED_VOL = "saved_vol"
    private const val DEFAULT_C = 40

    fun thresholdC() = DEFAULT_C

    fun tempC(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val tenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE) ?: Int.MIN_VALUE
        return if (tenths == Int.MIN_VALUE) 25 else tenths / 10
    }

    fun active(context: Context): Boolean {
        if (force(context)) return true
        if (prefs(context).getBoolean(KEY_DISABLED, false)) {
            if (tempC(context) < thresholdC() - 2) {
                prefs(context).edit().putBoolean(KEY_DISABLED, false).apply()
            } else {
                return false
            }
        }
        return tempC(context) >= thresholdC()
    }

    fun force(context: Context) = prefs(context).getBoolean(KEY_FORCE, false)

    fun capPercent(context: Context) = prefs(context).getInt(KEY_CAP, 45).coerceIn(20, 70)

    fun apply(context: Context): Boolean {
        if (!active(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val capIdx = (max * capPercent(context)) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > capIdx) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, capIdx, 0)
        }
        if (!BatterySaverDsp.enabled(context)) {
            BatterySaverDsp.setEnabled(context, true)
        }
        return true
    }

    fun startForce(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        prefs(context).edit()
            .putBoolean(KEY_FORCE, true)
            .putBoolean(KEY_DISABLED, false)
            .putInt(KEY_SAVED_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .putInt(KEY_CAP, 45)
            .apply()
        apply(context)
    }

    fun stop(context: Context) {
        val saved = prefs(context).getInt(KEY_SAVED_VOL, -1)
        prefs(context).edit()
            .putBoolean(KEY_FORCE, false)
            .putBoolean(KEY_DISABLED, true)
            .apply()
        if (saved >= 0) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0)
        }
    }

    fun cycle(context: Context): String {
        return when {
            !active(context) && !prefs(context).getBoolean(KEY_DISABLED, false) -> {
                startForce(context); label(context)
            }
            force(context) || active(context) -> {
                stop(context); label(context)
            }
            else -> {
                prefs(context).edit().putBoolean(KEY_DISABLED, false).apply()
                label(context)
            }
        }
    }

    fun label(context: Context): String {
        val t = tempC(context)
        return when {
            force(context) -> "Warmte-hold geforceerd ${capPercent(context)}% (${t}°C)"
            prefs(context).getBoolean(KEY_DISABLED, false) && t >= thresholdC() ->
                "Warmte-hold uit tot koelen (${t}°C)"
            active(context) -> "Warmte-hold aan ${t}°C≥${thresholdC()}°C cap ${capPercent(context)}%"
            else -> "Warmte-hold uit (${t}°C)"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
