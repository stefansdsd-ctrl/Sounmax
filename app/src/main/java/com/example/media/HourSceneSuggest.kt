package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Fallback-suggestie op uur + weekdag als usage nog leeg is. */
object HourSceneSuggest {
    fun suggest(context: Context): ListeningScene? {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val weekend = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).let {
            it == Calendar.SATURDAY || it == Calendar.SUNDAY
        }
        val id = when {
            hour in 6..8 && !weekend -> "commute"
            hour in 9..11 && !weekend -> "openkantoor"
            hour in 12..13 -> "restaurant"
            hour in 14..16 && !weekend -> "focus"
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
