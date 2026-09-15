package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Podcast: mid-boost, bass-cut, ANC adaptive. */
object PodcastOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "podcast_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Podcast uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("podcast")
            ?: SceneLookup.byId("speech")
            ?: SceneLookup.byId("gesprek")
            ?: SceneLookup.byId("library")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "podcast")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.ADAPTIVE)
        OneTapProfiles.apply(context, "podcast")
        DspControlService.start(context)
        Toast.makeText(context, "Podcast · mid-boost · 1.0×/1.2×", Toast.LENGTH_SHORT).show()
    }
}
