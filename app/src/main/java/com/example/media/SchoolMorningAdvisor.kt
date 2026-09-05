package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Ma–vr 07:00–08:30: schoolochtend i.p.v. standaard commute. */
object SchoolMorningAdvisor {
    const val KEY_ENABLED = "school_morning_advisor"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) return scene
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val mins = hour * 60 + minute
        if (mins !in 7 * 60..8 * 60 + 30) return scene
        return SceneLookup.byId("schoolochtend") ?: scene
    }
}
