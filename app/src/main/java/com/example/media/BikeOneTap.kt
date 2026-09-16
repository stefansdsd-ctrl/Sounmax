package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Fiets: wind-guard + veilig volume. */
object BikeOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "bike_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Fiets uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("avondfiets")
            ?: SceneLookup.byId("bike")
            ?: SceneLookup.byId("outdoor")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "avondfiets")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.WIND_GUARD)
        OneTapProfiles.apply(context, "bike")
        DspControlService.start(context)
        Toast.makeText(context, "Fiets · wind-guard", Toast.LENGTH_SHORT).show()
    }
}
