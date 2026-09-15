package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Game: punchy bass, ANC off voor lage latency-gevoel, volume-cap 80. */
object GameOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "game_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Game uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("game")
            ?: SceneLookup.byId("party")
            ?: SceneLookup.byId("focus")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "party")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.OFF)
        OneTapProfiles.apply(context, "game")
        DspControlService.start(context)
        Toast.makeText(context, "Game · bass + ANC uit", Toast.LENGTH_SHORT).show()
    }
}
