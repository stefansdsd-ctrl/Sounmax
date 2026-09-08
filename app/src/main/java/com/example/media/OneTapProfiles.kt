package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.dsp.AncMode
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/**
 * Eén-tik profielen voor dagelijkse situaties.
 * Combineert scene + ANC + optionele volume-cap.
 * Meest recent gebruikte profielen komen vooraan.
 */
object OneTapProfiles {
    const val PREFS = "soundmax_prefs"
    const val KEY_LAST = "one_tap_last"
    private const val KEY_ORDER = "one_tap_recent_csv"

    data class Profile(
        val id: String,
        val label: String,
        val sceneId: String,
        val anc: AncMode,
        val maxVolumePct: Int?
    )

    val all = listOf(
        Profile("commute", "Woon-werk", "commute", AncMode.STRONG, 80),
        Profile("focus", "Focus", "focus", AncMode.STRONG, null),
        Profile("sleep", "Slaap", "sleep", AncMode.ADAPTIVE, 40),
        Profile("outdoor", "Buiten", "outdoor", AncMode.WIND_GUARD, 70),
        Profile("talk", "Gesprek", "ambient", AncMode.AMBIENT, null),
        Profile("gym", "Sport", "sportschool", AncMode.ADAPTIVE, 85),
        Profile("train", "Trein", "intercity", AncMode.STRONG, 75),
        Profile("cafe", "Café", "koffietent", AncMode.AMBIENT, 60),
        Profile("bike", "Fiets", "avondfiets", AncMode.WIND_GUARD, 70),
        Profile("home", "Thuis", "thuisavond", AncMode.OFF, 55),
        Profile("shop", "Winkelen", "mall", AncMode.AMBIENT, 60),
        Profile("school", "School", "schoolochtend", AncMode.AMBIENT, 55),
        Profile("kids", "Kids", "thuiskids", AncMode.AMBIENT, 45),
        Profile("lib", "Bieb", "library", AncMode.STRONG, 40),
        Profile("off", "Uit", "default", AncMode.OFF, null)
    )

    fun byId(id: String): Profile? = all.find { it.id == id }

    fun ranked(context: Context): List<Profile> {
        val csv = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ORDER, "") ?: ""
        val recent = csv.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        val index = recent.withIndex().associate { it.value to it.index }
        return all.sortedBy { index[it.id] ?: Int.MAX_VALUE }
    }

    fun apply(context: Context, id: String): Boolean {
        val p = byId(id) ?: return false
        val scene: ListeningScene? = SceneLookup.byId(p.sceneId)
        SoftwareAnc.applyWithHardware(context, p.anc)
        p.maxVolumePct?.let { capVolume(context, it) }
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val prev = prefs.getString(KEY_ORDER, "") ?: ""
        val next = (listOf(id) + prev.split(',').map { it.trim() }.filter { it.isNotEmpty() && it != id })
            .take(20)
            .joinToString(",")
        prefs.edit()
            .putString(KEY_LAST, id)
            .putString(KEY_ORDER, next)
            .apply()
        lastSceneHint = scene?.id ?: p.sceneId
        return true
    }

    var lastSceneHint: String? = null
        private set

    fun lastId(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LAST, null)

    private fun capVolume(context: Context, pct: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (max * pct.coerceIn(10, 100) / 100f).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > target) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        }
    }
}
