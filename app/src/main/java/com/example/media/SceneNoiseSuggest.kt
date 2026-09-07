package com.example.media

import android.content.Context
import com.example.dsp.AmbientNoiseFloor
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import com.google.android.gms.location.DetectedActivity

/**
 * Stelt een luisterscene voor op basis van ruisvloer + laatste activiteit.
 * Geen extra permissies. Opt-in via auto_fusion prefs.
 */
object SceneNoiseSuggest {
    data class Suggestion(
        val sceneId: String,
        val reason: String,
        val noise: Float,
        val noiseLabel: String
    )

    fun suggest(context: Context, current: ListeningScene): Suggestion? {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("scene_noise_suggest", true)) return null

        val actType = prefs.getInt("last_activity_type", -1)
        val actName = when (actType) {
            DetectedActivity.IN_VEHICLE -> "in_vehicle"
            DetectedActivity.ON_BICYCLE -> "on_bicycle"
            DetectedActivity.RUNNING -> "running"
            DetectedActivity.WALKING, DetectedActivity.ON_FOOT -> "walking"
            DetectedActivity.STILL -> "still"
            else -> null
        }
        val outdoor = prefs.getBoolean("last_place_outdoor", false)
        val rssi = prefs.getInt("last_headset_rssi", Int.MIN_VALUE)
            .takeIf { it != Int.MIN_VALUE }

        val noise = AmbientNoiseFloor.estimate(context, actName, rssi, outdoor)
        val id = pickSceneId(actType, noise) ?: return null
        if (id == current.id) return null

        val suggestion = Suggestion(
            sceneId = id,
            reason = "ruis ${AmbientNoiseFloor.lastLabel} + ${ActivitySceneMonitor.labelFor(actType)}",
            noise = noise,
            noiseLabel = AmbientNoiseFloor.lastLabel
        )
        prefs.edit()
            .putString("last_noise_suggest_id", id)
            .putString("last_noise_suggest_reason", suggestion.reason)
            .apply()
        return suggestion
    }

    fun adjust(context: Context, current: ListeningScene): ListeningScene {
        val s = suggest(context, current) ?: return current
        return SceneLookup.byId(s.sceneId) ?: current
    }

    private fun pickSceneId(actType: Int, noise: Float): String? {
        val loud = noise >= 0.75f
        val busy = noise >= 0.50f
        val quiet = noise < 0.30f
        return when (actType) {
            DetectedActivity.IN_VEHICLE -> when {
                loud -> "commute"
                quiet -> "nightdrive"
                else -> "car"
            }
            DetectedActivity.ON_BICYCLE -> if (loud) "avondfiets" else "bike"
            DetectedActivity.RUNNING -> "cardio"
            DetectedActivity.WALKING, DetectedActivity.ON_FOOT -> when {
                loud -> "walk"
                quiet -> "podcastwalk"
                else -> "avondwandeling"
            }
            DetectedActivity.STILL -> when {
                loud -> "office"
                quiet && busy.not() -> "thuisavond"
                quiet -> "focus"
                else -> "office"
            }
            else -> when {
                loud -> "commute"
                quiet -> "focus"
                else -> null
            }
        }
    }
}
