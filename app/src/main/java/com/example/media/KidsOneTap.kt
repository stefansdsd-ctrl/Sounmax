package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Kids: transparantie + veilig volume. */
object KidsOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "kids_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Kids uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("thuiskids")
            ?: SceneLookup.byId("baby")
            ?: SceneLookup.byId("ambient")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "thuiskids")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.AMBIENT)
        OneTapProfiles.apply(context, "kids")
        DspControlService.start(context)
        Toast.makeText(context, "Kids · transparantie + veilig volume", Toast.LENGTH_SHORT).show()
    }
}
