package com.example.data

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager

/**
 * Android Auto / car-ui: muziek boven 80% → 62%.
 * Voorkomt een klap bij overschakelen naar de auto.
 */
object CarCap {
    private const val PREFS = "sounmax_car_cap"
    private const val CAP_PCT = 62
    private const val TRIGGER_PCT = 80

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inCar(context: Context): Boolean {
        val ui = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager ?: return false
        return ui.currentModeType == Configuration.UI_MODE_TYPE_CAR
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inCar(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("capped", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && inCar(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Auto-cap uit"
        inCar(context) -> "Auto-cap → 62%"
        else -> "Auto-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
