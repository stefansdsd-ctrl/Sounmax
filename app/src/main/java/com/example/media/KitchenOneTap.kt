package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Koken: ambient/talk-through zodat je de keuken hoort, cap 60. */
object KitchenOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "kitchen_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Koken uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("cook")
            ?: SceneLookup.byId("koffietent")
            ?: SceneLookup.byId("ambient")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "ambient")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.AMBIENT)
        OneTapProfiles.apply(context, "kitchen")
        DspControlService.start(context)
        Toast.makeText(context, "Koken · ambient", Toast.LENGTH_SHORT).show()
    }
}
