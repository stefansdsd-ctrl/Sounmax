package com.example.media

import android.content.Context
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget

/**
 * Eén tik: rust-scene, ANC uit, veilig volume, 15 min pauze-timer.
 */
object EarRestOneTap {
    fun apply(context: Context): String {
        val scene = SceneLookup.byId("rest") ?: ListeningScenes.ALL.first { it.id == "rest" }
        val minutes = 15
        val until = System.currentTimeMillis() + minutes * 60_000L
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit()
            .putString("last_scene_id", scene.id)
            .putString("last_anc", AncMode.OFF.name)
            .putBoolean("pending_widget_scene", true)
            .putLong(SoundMaxWidget.KEY_SLEEP_END, until)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, minutes)
            .putBoolean("pending_widget_sleep", true)
            .apply()
        NightVolumeGuard.setEnabled(context, true)
        NightVolumeGuard.applyIfNeeded(context)
        SleepFade.schedule(context, until)
        DspControlService.start(context)
        return "Oorpauze: ${scene.name} · 15m · volume-cap"
    }
}
