package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Concert: live-ruimte + punch. */
object ConcertOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "concert_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Concert uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("concert") ?: SceneLookup.byId("concertzaal")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "concert")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.ADAPTIVE)
        OneTapProfiles.apply(context, "concert")
        DspControlService.start(context)
        Toast.makeText(context, "Concert · adaptive", Toast.LENGTH_SHORT).show()
    }
}
