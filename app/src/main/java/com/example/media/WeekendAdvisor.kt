package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Weekend: markt, terras, zondagochtend i.p.v. commute. */
object WeekendAdvisor {
    const val KEY_ENABLED = "weekend_advisor"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        if (dow != Calendar.SATURDAY && dow != Calendar.SUNDAY) return scene
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val id = when {
            dow == Calendar.SATURDAY && hour in 8..12 -> "weekendmarkt"
            dow == Calendar.SATURDAY && hour in 13..18 -> "terrasavond"
            dow == Calendar.SATURDAY && hour >= 19 -> "thuisavond"
            dow == Calendar.SUNDAY && hour in 8..12 -> "zondagochtend"
            dow == Calendar.SUNDAY && hour >= 19 -> "thuisavond"
            else -> return scene
        }
        return SceneLookup.byId(id) ?: scene
    }
}
