package com.example.media

import android.content.Context
import android.media.AudioManager

/**
 * Dagelijks gehoorbudget: 1 uur-equivalent op 80 dB = 100%.
 * Soft-cap volume bij ≥ 100%.
 */
object DailyHearingBudget {
    const val KEY_ENABLED = "daily_hearing_budget"
    private const val DAY_CAP_EQ_MIN = 60f

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun todayEqMinutes(context: Context): Float =
        ListenDose.weekMinutes(context).lastOrNull()?.second ?: 0f

    fun percent(context: Context): Int =
        ((todayEqMinutes(context) / DAY_CAP_EQ_MIN) * 100f).toInt().coerceAtLeast(0)

    fun chipLabel(context: Context): String =
        "${percent(context).coerceAtMost(999)}% van 80 dB-uur"

    fun applySoftCap(context: Context): Boolean {
        if (!enabled(context) || percent(context) < 100) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.70f).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            return true
        }
        return false
    }
}
