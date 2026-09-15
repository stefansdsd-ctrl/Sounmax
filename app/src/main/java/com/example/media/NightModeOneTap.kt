package com.example.media

import android.content.Context
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget

/**
 * Eén tik: slaap-scene, stille uren, nacht-volume, 45 min slaaptimer.
 */
object NightModeOneTap {
    fun apply(context: Context): String {
        val scene = SceneLookup.byId("sleep") ?: ListeningScenes.ALL.first { it.id == "sleep" }
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit()
            .putString("last_scene_id", scene.id)
            .putString("last_anc", AncMode.OFF.name)
            .putBoolean("pending_widget_scene", true)
            .apply()
        QuietHours.setEnabled(context, true)
        NightVolumeGuard.setEnabled(context, true)
        NightVolumeGuard.applyIfNeeded(context)
        val minutes = 45
        val until = System.currentTimeMillis() + minutes * 60_000L
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit()
            .putLong(SoundMaxWidget.KEY_SLEEP_END, until)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, minutes)
            .putBoolean("pending_widget_sleep", true)
            .apply()
        SleepFade.schedule(context, until)
        DspControlService.start(context)
        return "Nachtmodus: ${scene.name} · stil · 45m"
    }
}
