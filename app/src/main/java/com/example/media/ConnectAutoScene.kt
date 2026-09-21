package com.example.media

import android.content.Context
import com.example.dsp.BestNow
import com.example.widget.SoundMaxWidget

/** Bij headset-koppeling optioneel de “beste nu”-scene zetten. */
object ConnectAutoScene {
    private const val PREFS = "sounmax_connect_auto"
    private const val KEY = "enabled"
    private const val LAST = "last_ms"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, on).apply()
    }

    fun maybeApply(context: Context) {
        if (!enabled(context)) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(LAST, 0L) < 45_000L) return
        prefs.edit().putLong(LAST, now).apply()
        val scene = BestNow.top(context) ?: HourSceneSuggest.suggest(context) ?: return
        SoundMaxWidget.applyScene(context, scene.id)
    }
}
