package com.example.media

import android.content.Context
import com.example.dsp.StereoDynamics

/**
 * Eén-oor / mono mix: smaller stereo + veilig volume.
 * Handig bij fietsen of als één cup los zit.
 */
object OneEarMode {
    const val PREFS = SceneAutomation.PREFS
    const val KEY = "one_ear_mode"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    fun set(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, on).apply()
        apply(on)
        if (on) SafeVolume.enforce(context)
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        set(context, next)
        return next
    }

    fun apply(on: Boolean) {
        StereoDynamics.init()
        StereoDynamics.stereoWidth(if (on) 0f else 1f)
        StereoDynamics.safeLimiter(on)
    }

    fun label(context: Context): String =
        if (enabled(context)) "Eén oor aan" else "Eén oor uit"
}
