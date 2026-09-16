package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Auto: lage latency + wind/wegruis. */
object CarOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "car_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Auto uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("car") ?: SceneLookup.byId("nightdrive")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "car")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.WIND_GUARD)
        OneTapProfiles.apply(context, "car")
        DspControlService.start(context)
        Toast.makeText(context, "Auto · wind-guard", Toast.LENGTH_SHORT).show()
    }
}
