package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** College: stem-EQ + ambient, veilig volume. */
object CollegeOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "college_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "College uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("college") ?: SceneLookup.byId("schoolochtend")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "college")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.AMBIENT)
        OneTapProfiles.apply(context, "college")
        DspControlService.start(context)
        Toast.makeText(context, "College · stem + ambient", Toast.LENGTH_SHORT).show()
    }
}
