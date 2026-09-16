package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SoftwareAnc

/** Focus-chip: 25 min deep work + sterke ANC. */
object FocusOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "focus_chip_on"

    fun isOn(context: Context): Boolean =
        FocusSession.isActive(context) ||
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            FocusSession.cancel(context)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Focus uit", Toast.LENGTH_SHORT).show()
            return
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, true).apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "focus")
        FocusSession.start(context, 25)
    }
}
