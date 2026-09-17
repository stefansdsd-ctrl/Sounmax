package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Stelt accubesparing-scene voor bij lage headset-accu. */
object BatterySaverHint {
    private const val THRESHOLD = 20

    fun lastBattery(context: Context): Int =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt("last_battery", -1)

    fun shouldSuggest(context: Context): Boolean {
        val pct = lastBattery(context)
        return pct in 0 until THRESHOLD
    }

    fun scene(): ListeningScene? = SceneLookup.byId("batterysave")

    fun label(context: Context): String? {
        if (!shouldSuggest(context)) return null
        val pct = lastBattery(context)
        return "Accu $pct% → besparen"
    }
}
