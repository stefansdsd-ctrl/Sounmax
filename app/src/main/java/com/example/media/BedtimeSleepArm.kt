package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.widget.SoundMaxWidget
import java.util.Calendar

/**
 * Vanaf 22:00: als er muziek speelt en er geen timer loopt,
 * arm eenmaal per nacht een slaaptimer (45 min) + fade.
 */
object BedtimeSleepArm {
    const val KEY_ENABLED = "bedtime_sleep_arm"
    private const val KEY_ARMED_DAY = "bedtime_armed_day"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun maybeArm(context: Context) {
        if (!enabled(context)) return
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour < 22 && hour >= 6) return

        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (prefs.getInt(KEY_ARMED_DAY, -1) == day) return

        val end = prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
        if (SoundMaxWidget.remainingSleepMinutes(end) > 0) {
            prefs.edit().putInt(KEY_ARMED_DAY, day).apply()
            return
        }
        if (!isMusicLikelyPlaying(context)) return

        val minutes = 45
        val until = System.currentTimeMillis() + minutes * 60_000L
        prefs.edit()
            .putLong(SoundMaxWidget.KEY_SLEEP_END, until)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, minutes)
            .putInt(KEY_ARMED_DAY, day)
            .apply()
        SleepFade.schedule(context, until)
        SoundMaxWidget.refreshAll(context)
    }

    private fun isMusicLikelyPlaying(context: Context): Boolean = try {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.isMusicActive
    } catch (_: Exception) {
        false
    }
}
