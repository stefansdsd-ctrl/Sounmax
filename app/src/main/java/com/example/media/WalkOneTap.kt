package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Wandelen: outdoor + wind-guard, volume-cap 70. */
object WalkOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "walk_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Wandelen uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("outdoor")
            ?: SceneLookup.byId("avondfiets")
            ?: SceneLookup.byId("commute")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "outdoor")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.WIND_GUARD)
        OneTapProfiles.apply(context, "walk")
        DspControlService.start(context)
        Toast.makeText(context, "Wandelen · wind-guard", Toast.LENGTH_SHORT).show()
    }
}
