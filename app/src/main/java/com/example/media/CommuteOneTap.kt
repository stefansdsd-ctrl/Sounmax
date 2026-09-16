package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Pendelen: max ANC + commute-scene. */
object CommuteOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "commute_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Pendelen uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("commute") ?: SceneLookup.byId("train")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "commute")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "commute")
        DspControlService.start(context)
        Toast.makeText(context, "Pendelen · max ANC", Toast.LENGTH_SHORT).show()
    }
}
