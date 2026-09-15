package com.example.media

import android.content.Context
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget

/**
 * Eén tik: ochtend/pendel-scene, DSP aan, stilte + slaaptimer uit.
 */
object MorningBoostOneTap {
    fun apply(context: Context): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val id = if (hour in 6..10) "commute" else "wfh"
        val scene = SceneLookup.byId(id) ?: ListeningScenes.ALL.first { it.id == id }
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit()
            .putString("last_scene_id", scene.id)
            .putString("last_anc", AncMode.ADAPTIVE.name)
            .putBoolean("pending_widget_scene", true)
            .putLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, 0)
            .apply()
        QuietHours.setEnabled(context, false)
        NightVolumeGuard.setEnabled(context, false)
        SleepFade.cancel(context)
        val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
        ui.edit().putBoolean(DspControlService.KEY_DSP, true).apply()
        DspControlService.start(context)
        return "Ochtend: ${scene.name} · DSP · ANC adaptive"
    }
}
