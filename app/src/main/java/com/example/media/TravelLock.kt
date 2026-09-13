package com.example.media

import android.content.Context
import com.example.dsp.AncMode
import com.example.dsp.SoftwareAnc
import com.example.widget.SoundMaxWidget

/**
 * Reis-modus: woon-werk/trein-profiel + ANC sterk + auto-scene 45 min op slot.
 */
object TravelLock {
    const val PREFS = "soundmax_wellness"
    const val KEY_UNTIL = "manual_scene_until"
    const val HOLD_MS = 45L * 60_000L
    const val KEY_MINUTES = "travel_lock_minutes"

    fun lastMinutes(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_MINUTES, 45)
            .coerceIn(15, 180)

    fun isOn(context: Context): Boolean {
        val until = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_UNTIL, 0L)
        return until > System.currentTimeMillis()
    }

    fun minutesLeft(context: Context): Int {
        val until = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_UNTIL, 0L)
        val left = until - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 59_999) / 60_000).toInt()
    }

    fun toggle(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return if (isOn(context)) {
            prefs.edit().putLong(KEY_UNTIL, 0L).apply()
            false
        } else {
            val mins = lastMinutes(context)
            prefs.edit()
                .putLong(KEY_UNTIL, System.currentTimeMillis() + mins * 60_000L)
                .putInt(KEY_MINUTES, mins)
                .apply()
            val id = if (OneTapProfiles.lastId(context) in listOf("train", "commute")) {
                OneTapProfiles.lastId(context)!!
            } else "commute"
            OneTapProfiles.apply(context, id)
            SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
            SoundMaxWidget.applyScene(context, OneTapProfiles.byId(id)?.sceneId)
            true
        }
    }

    fun holdMinutes(context: Context, minutes: Int) {
        val m = minutes.coerceIn(1, 180)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_UNTIL, System.currentTimeMillis() + m * 60_000L)
            .putInt(KEY_MINUTES, m)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_UNTIL, 0L).apply()
    }
}
