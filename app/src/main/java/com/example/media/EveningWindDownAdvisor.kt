package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Avond: 22:00–06:00 → thuisavond / sleep i.p.v. commute/office. */
object EveningWindDownAdvisor {
    const val KEY_ENABLED = "evening_winddown"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val id = when {
            hour >= 23 || hour < 6 -> "sleep"
            hour >= 22 -> "thuisavond"
            else -> return scene
        }
        if (scene.id in setOf("sleep", "thuisavond", "nacht", "rest", "asmr", "baby")) return scene
        return SceneLookup.byId(id) ?: scene
    }
}
