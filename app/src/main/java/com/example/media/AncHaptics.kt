package com.example.media

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.dsp.AncMode
import com.example.dsp.SceneGroups

/** Korte haptic bij ANC-wissel en scene-groep. */
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
        val ms = when (mode) {
            AncMode.OFF -> 18L
            AncMode.AMBIENT -> 30L
            AncMode.STRONG -> 55L
            AncMode.ADAPTIVE -> 40L
            AncMode.WIND_GUARD -> 45L
        }
        oneShot(context, ms)
    }

    /** 1=werk, 2=outdoor, 3=slaap. */
    fun sceneConfirm(context: Context, sceneId: String?) {
        val id = sceneId ?: return
        val ticks = when {
            SceneGroups.ids("Nacht")?.contains(id) == true -> 3
            SceneGroups.ids("Onderweg")?.contains(id) == true ||
                SceneGroups.ids("Sport")?.contains(id) == true -> 2
            SceneGroups.ids("Werk")?.contains(id) == true -> 1
            else -> 1
        }
        tick(context, ticks)
    }

    fun tick(context: Context, count: Int) {
        if (!hapticEnabled(context)) return
        val vib = vibrator(context) ?: return
        val n = count.coerceIn(1, 3)
        runCatching {
            if (Build.VERSION.SDK_INT >= 26) {
                val timings = LongArray(n * 2) { i -> if (i % 2 == 0) 18L else 70L }
                val amps = IntArray(n * 2) { i -> if (i % 2 == 0) 180 else 0 }
                vib.vibrate(VibrationEffect.createWaveform(timings, amps, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(18L * n)
            }
        }
    }

    private fun oneShot(context: Context, ms: Long) {
        val vib = vibrator(context) ?: return
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
