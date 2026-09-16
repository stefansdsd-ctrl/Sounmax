package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Museum: zacht, ruim, audioguide. */
object MuseumOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "museum_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Museum uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("museum")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "museum")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.OFF)
        OneTapProfiles.apply(context, "museum")
        DspControlService.start(context)
        Toast.makeText(context, "Museum · zacht + ruim", Toast.LENGTH_SHORT).show()
    }
}
