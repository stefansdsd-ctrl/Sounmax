package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Winkelen: transparantie in de hal. */
object ShopOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "shop_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Winkelen uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("mall")
            ?: SceneLookup.byId("shop")
            ?: SceneLookup.byId("supermarket")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "mall")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.AMBIENT)
        OneTapProfiles.apply(context, "shop")
        DspControlService.start(context)
        Toast.makeText(context, "Winkelen · transparantie", Toast.LENGTH_SHORT).show()
    }
}
