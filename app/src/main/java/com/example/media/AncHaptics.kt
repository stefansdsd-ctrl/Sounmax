package com.example.media

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.dsp.AncMode

/** Korte haptic bij ANC-wissel. Uit te zetten via prefs. */
object AncHaptics {
    private const val PREFS = "sounmax_feel"
    const val KEY_HAPTIC = "anc_haptic"
    const val KEY_OLED = "oled_black"

    fun hapticEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_HAPTIC, true)

    fun setHaptic(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_HAPTIC, on).apply()
    }

    fun oledEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_OLED, false)

    fun setOled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_OLED, on).apply()
    }

    fun pulse(context: Context, mode: AncMode) {
        if (!hapticEnabled(context)) return
        val vib = vibrator(context) ?: return
        val ms = when (mode) {
            AncMode.OFF -> 18L
            AncMode.AMBIENT -> 30L
            AncMode.STRONG -> 55L
            AncMode.ADAPTIVE -> 40L
            AncMode.WIND_GUARD -> 45L
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= 26) {
                vib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(ms)
            }
        }
    }

    private fun vibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= 31) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
}
