package com.example.media

import android.content.Context
import com.example.dsp.AmbientNoiseFloor
import com.example.dsp.AncMode
import com.example.dsp.SoftwareAnc

/**
 * Auto-ANC: map AmbientNoiseFloor → AncMode.
 * Stil → OFF, gemiddeld → ADAPTIVE, druk → STRONG, buiten/luid → WIND_GUARD.
 */
object AutoAnc {
    private const val PREFS = "soundmax_wellness"
    const val KEY_ENABLED = "auto_anc"
    const val KEY_SENS = "auto_anc_sens"
    private var lastApplyMs = 0L
    private var lastMode: AncMode? = null

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    /** 0 = rustig (later ANC), 1 = normaal, 2 = scherp (eerder ANC). */
    fun sensitivity(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_SENS, 1).coerceIn(0, 2)

    fun setSensitivity(context: Context, level: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SENS, level.coerceIn(0, 2)).apply()
    }

    fun suggest(intensity: Float, outdoor: Boolean = false, sens: Int = 1): AncMode {
        val shift = when (sens) {
            0 -> 0.12f
            2 -> -0.10f
            else -> 0f
        }
        val n = (intensity + shift).coerceIn(0f, 1f)
        return when {
            outdoor && n >= 0.45f -> AncMode.WIND_GUARD
            n >= 0.72f -> AncMode.STRONG
            n >= 0.42f -> AncMode.ADAPTIVE
            n >= 0.22f -> AncMode.AMBIENT
            else -> AncMode.OFF
        }
    }

    fun tick(
        context: Context,
        apply: (AncMode) -> Unit,
        outdoor: Boolean = false,
        activity: String? = null,
        rssi: Int? = null
    ) {
        if (!enabled(context)) return
        val noise = AmbientNoiseFloor.estimate(context, activity, rssi, outdoor)
        val mode = suggest(noise, outdoor, sensitivity(context))
        val now = System.currentTimeMillis()
        if (mode == lastMode && now - lastApplyMs < 12_000L) return
        if (now - lastApplyMs < 4_000L) return
        lastMode = mode
        lastApplyMs = now
        apply(mode)
        SoftwareAnc.applyWithHardware(context, mode)
    }

    fun label(context: Context): String {
        val on = enabled(context)
        val n = AmbientNoiseFloor.lastLabel
        return if (on) "Auto-ANC · $n" else "Auto-ANC uit"
    }
}
