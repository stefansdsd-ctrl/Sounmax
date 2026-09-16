package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Sportschool: adaptive ANC, volume-cap 85. */
object GymOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "gym_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Sport uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("sportschool")
            ?: SceneLookup.byId("gym")
            ?: SceneLookup.byId("outdoor")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "sportschool")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.ADAPTIVE)
        OneTapProfiles.apply(context, "gym")
        DspControlService.start(context)
        Toast.makeText(context, "Sport · adaptive ANC", Toast.LENGTH_SHORT).show()
    }
}
