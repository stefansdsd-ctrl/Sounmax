package com.example.media

import android.content.Context
import com.example.dsp.ConversationBoost

/**
 * Tijdelijke gespreksmodus als omgevings-spraak hard is.
 * Geen GATT nodig: mic-RMS + ConversationBoost.
 */
object TalkThrough {
    const val KEY_ENABLED = "talk_through"
    private const val PREFS = SceneAutomation.PREFS
    private const val HOLD_MS = 8_000L
    @Volatile private var lastTrigger = 0L
    @Volatile private var active = false

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
        if (!on) release()
    }

    fun isActive(): Boolean = active

    /** [rms] 0..1. Drempel ~spraak op 1 m. */
    fun onRms(rms: Float) {
        if (rms < 0.18f) {
            if (active && System.currentTimeMillis() - lastTrigger > HOLD_MS) release()
            return
        }
        lastTrigger = System.currentTimeMillis()
        if (!active) {
            active = true
            ConversationBoost.apply(true)
        }
    }

    fun release() {
        if (!active) return
        active = false
        ConversationBoost.apply(false)
    }

    fun chipLabel(): String =
        if (active) "Spraak aan" else "Talk-through"
}
