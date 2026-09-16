package com.example.media

import android.content.Context
import android.widget.Toast

/** Slaap-chip: nachtmodus + 45 min fade. */
object SleepOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "sleep_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            QuietHours.setEnabled(context, false)
            Toast.makeText(context, "Slaap uit", Toast.LENGTH_SHORT).show()
            return
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, true).apply()
        OneTapProfiles.apply(context, "sleep")
        val msg = NightModeOneTap.apply(context)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
