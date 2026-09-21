package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.data.SceneUsage
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
            "wind" in weather -> "polderweg"
            else -> null
        }
        if (weatherId != null) SceneLookup.byId(weatherId)?.let { return it }
        SceneUsage.suggestNow(context)?.let { used ->
            if (SceneUsage.count(context, used.id) >= 2) return used
        }

        val id = when {
            hour in 6..8 && !weekend -> "ovchippoortplus"
            hour in 9..11 && !weekend -> "zolderwerk"
            hour in 12..13 && !weekend -> "kantinehal"
            hour in 12..16 && weekend -> "sluiswacht"
            hour in 14..16 && !weekend -> "apotheekwacht"
            hour in 17..18 && !weekend -> "regenperron"
            hour in 19..20 -> "avondcollege"
            hour in 21..22 && !weekend -> "thuisfilmlaat"
            hour in 20..23 && weekend -> "thuisfilmlaat"
            hour in 17..19 && weekend -> "parkeerdek"
            hour in 22..23 -> "nachtwinkel"
            hour < 6 -> "nachtwerk"
            weekend && hour in 9..11 -> "brievenbusrij"
            weekend && hour in 9..12 -> "buurtsuper"
            else -> "afterwork"
        }
        return SceneLookup.byId(id)
    }

    fun label(context: Context): String? =
        suggest(context)?.let { "Rond nu: ${it.name}" }
}
