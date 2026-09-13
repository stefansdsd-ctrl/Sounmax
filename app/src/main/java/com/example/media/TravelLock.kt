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
            prefs.edit().putLong(KEY_UNTIL, System.currentTimeMillis() + HOLD_MS).apply()
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
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_UNTIL, System.currentTimeMillis() + minutes.coerceIn(1, 180) * 60_000L)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_UNTIL, 0L).apply()
    }
}
