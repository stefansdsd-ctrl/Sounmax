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
    private var lastApplyMs = 0L
    private var lastMode: AncMode? = null

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun suggest(intensity: Float, outdoor: Boolean = false): AncMode = when {
        outdoor && intensity >= 0.45f -> AncMode.WIND_GUARD
        intensity >= 0.72f -> AncMode.STRONG
        intensity >= 0.42f -> AncMode.ADAPTIVE
        intensity >= 0.22f -> AncMode.AMBIENT
        else -> AncMode.OFF
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
        val mode = suggest(noise, outdoor)
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
