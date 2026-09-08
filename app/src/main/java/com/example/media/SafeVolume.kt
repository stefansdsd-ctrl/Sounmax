package com.example.media

import android.content.Context
import android.media.AudioManager
import android.util.Log

/**
 * Hard cap op STREAM_MUSIC als "Veilig volume" aan staat.
 * Standaard max 60% van het systeemvolume.
 */
object SafeVolume {
    private const val TAG = "SafeVolume"
    const val KEY = "safe_volume"
    const val KEY_CAP = "safe_volume_cap_pct"
    const val DEFAULT_CAP_PCT = 60

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)

    fun capPct(context: Context): Int =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_CAP, DEFAULT_CAP_PCT).coerceIn(30, 90)

    fun enforce(context: Context): Boolean {
        if (!enabled(context) && !QuietHours.isQuietNow(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cap = ((max * capPct(context)) / 100).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            Log.i(TAG, "volume $cur → $cap (cap ${capPct(context)}%)")
            return true
        }
        return false
    }
}
