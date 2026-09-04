package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

object CommuteAdvisor {
    const val KEY_ENABLED = "commute_advisor"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        if (!isCommuteWindow()) return scene
        val rainy = WeatherAdvisor.lastLabel(context)?.contains("regen", ignoreCase = true) == true
        val id = if (rainy) "commute_rain" else "commute"
        return SceneLookup.byId(id) ?: scene
    }

    fun isCommuteWindow(): Boolean {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) return false
        val minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val morning = minutes in (6 * 60 + 20)..(9 * 60 + 30)
        val evening = minutes in (16 * 60 + 20)..(19 * 60 + 30)
        return morning || evening
    }
}
