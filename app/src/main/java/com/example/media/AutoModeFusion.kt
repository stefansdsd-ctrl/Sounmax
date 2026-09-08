package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/**
 * Combineert activiteit, agenda, plaats, wifi-fingerprint, weer, ruisvloer en wind tot één scene.
 * Hoogste gewicht wint; bij gelijkspel blijft de huidige scene.
 */
object AutoModeFusion {
    data class Vote(val sceneId: String, val weight: Int, val reason: String)

    fun adjust(context: Context, current: ListeningScene): ListeningScene {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("auto_fusion", true)) return current

        val votes = mutableListOf<Vote>()

        val actType = prefs.getInt("last_activity_type", -1)
        ActivitySceneMonitor.sceneIdFor(actType)?.let {
            votes += Vote(it, 8, "activiteit")
        }

        val cal = CalendarMeetingAdvisor.adjust(context, current)
        if (cal.id != current.id) votes += Vote(cal.id, 10, "agenda")

        val geo = GeofencePlaceAdvisor.adjust(context, current)
        if (geo.id != current.id) votes += Vote(geo.id, 7, "plaats")

        val wifi = WifiPlaceAdvisor.adjust(context, current)
        if (wifi.id != current.id) votes += Vote(wifi.id, 6, "wifi")

        val rssi = WifiRssiMap.adjust(context, current)
        if (rssi.id != current.id) votes += Vote(rssi.id, 9, "wifi-fingerprint")

        val weather = WeatherAdvisor.suggest(context, current)
        if (weather.id != current.id) votes += Vote(weather.id, 4, "weer")

        val commute = CommuteAdvisor.adjust(context, current)
        if (commute.id != current.id) votes += Vote(commute.id, 7, "pendel")

        val noise = SceneNoiseSuggest.suggest(context, current)
        if (noise != null) votes += Vote(noise.sceneId, 9, noise.reason)

        val wind = WindAdvisor.suggest(context, current)
        if (wind != null) votes += Vote(wind.id, 11, "wind")

        if (votes.isEmpty()) return current

        val best = votes.groupBy { it.sceneId }
            .mapValues { e -> e.value.sumOf { it.weight } to e.value.joinToString("+") { it.reason } }
            .maxByOrNull { it.value.first } ?: return current

        if (best.value.first < 8) return current
        prefs.edit().putString("auto_fusion_reasons", best.value.second).apply()
        val next = SceneLookup.byId(best.key) ?: current
        SceneVolumeCap.apply(context, next)
        return next
    }

    fun lastReasons(context: Context): String {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        return prefs.getString("auto_fusion_reasons", "") ?: ""
    }
}
