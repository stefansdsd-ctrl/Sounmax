package com.example.media

import android.content.Context
import com.example.data.SceneUsage
import com.example.dsp.BestNow
import com.example.widget.SoundMaxWidget

/** Bij headset-koppeling: recente scene herstellen, anders “beste nu”. */
object ConnectAutoScene {
    private const val PREFS = "sounmax_connect_auto"
    private const val KEY = "enabled"
    private const val LAST = "last_ms"
    private const val RESTORE_MS = 20 * 60_000L

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

        val usage = context.getSharedPreferences("sounmax_scene_usage", Context.MODE_PRIVATE)
        val recent = SceneUsage.recent(context, 1).firstOrNull()
        val lastTs = recent?.let { usage.getLong("last_${it.id}", 0L) } ?: 0L
        val sceneId = if (recent != null && lastTs > 0L && now - lastTs < RESTORE_MS) {
            recent.id
        } else {
            (BestNow.top(context) ?: HourSceneSuggest.suggest(context))?.id
        } ?: return
        SoundMaxWidget.applyScene(context, sceneId)
    }
}
