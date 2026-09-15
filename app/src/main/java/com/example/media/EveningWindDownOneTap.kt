package com.example.media

import android.content.Context
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget
import java.util.Calendar

/**
 * Eén tik: avond-afbouw. 22h+ slaap, anders thuisavond + volume-cap + ANC zacht.
 */
object EveningWindDownOneTap {
    fun apply(context: Context): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val id = if (hour >= 22 || hour < 6) "sleep" else "thuisavond"
        val scene = SceneLookup.byId(id)
            ?: ListeningScenes.ALL.firstOrNull { it.id == id }
            ?: ListeningScenes.ALL.first()
        val anc = if (id == "sleep") AncMode.OFF else AncMode.ADAPTIVE
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit()
            .putString("last_scene_id", scene.id)
            .putString("last_anc", anc.name)
            .putBoolean("pending_widget_scene", true)
            .apply()
        QuietHours.setEnabled(context, hour >= 22 || hour < 6)
        NightVolumeGuard.setEnabled(context, true)
        NightVolumeGuard.applyIfNeeded(context)
        OneTapProfiles.apply(context, if (id == "sleep") "sleep" else "home")
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        return "Avond: ${scene.name} · ANC ${anc.displayName}"
    }
}
