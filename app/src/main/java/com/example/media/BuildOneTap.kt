package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Bouw/wegwerk: max ANC. */
object BuildOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "build_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Bouw uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("bouw")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "bouw")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "build")
        DspControlService.start(context)
        Toast.makeText(context, "Bouw · max ANC", Toast.LENGTH_SHORT).show()
    }
}
