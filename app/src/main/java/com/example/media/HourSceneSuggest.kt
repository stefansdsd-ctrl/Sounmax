package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Fallback-suggestie op uur + weekdag + weer als usage nog leeg is. */
object HourSceneSuggest {
    fun suggest(context: Context): ListeningScene? {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val weekend = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).let {
            it == Calendar.SATURDAY || it == Calendar.SUNDAY
        }
        val weather = WeatherAdvisor.lastLabel(context)?.lowercase().orEmpty()
        val weatherId = when {
            "onweer" in weather || "thunder" in weather -> "storm"
            "regen" in weather || "rain" in weather -> if (hour in 7..9 || hour in 16..18) "windfiets" else "rain"
            "wind" in weather -> "windfiets"
            else -> null
        }
        if (weatherId != null) SceneLookup.byId(weatherId)?.let { return it }

        val id = when {
            hour in 6..8 && !weekend -> "commute"
            hour in 9..11 && !weekend -> "openkantoor"
            hour in 12..13 -> "restaurant"
            hour in 14..16 && !weekend -> "pomodoro"
            hour in 17..18 && !weekend -> "commute"
            hour in 19..21 -> "thuisbios"
            hour in 22..23 || hour < 6 -> "nachtwerk"
            weekend && hour in 9..12 -> "walk"
            else -> "afterwork"
        }
        return SceneLookup.byId(id)
    }

    fun label(context: Context): String? =
        suggest(context)?.let { "Rond nu: ${it.name}" }
}
