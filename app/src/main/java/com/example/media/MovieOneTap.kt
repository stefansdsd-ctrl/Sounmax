package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Film: brede scene, max ANC, volume-cap 75. */
object MovieOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "movie_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Film uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("film")
            ?: SceneLookup.byId("movie")
            ?: SceneLookup.byId("thuisavond")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "film")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "movie")
        DspControlService.start(context)
        Toast.makeText(context, "Film · ANC max · 75%", Toast.LENGTH_SHORT).show()
    }
}
