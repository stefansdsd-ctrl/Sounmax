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
 */
object OneTapProfiles {
    const val PREFS = "soundmax_prefs"
    const val KEY_LAST = "one_tap_last"

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
        Profile("off", "Uit", "default", AncMode.OFF, null)
    )

    fun byId(id: String): Profile? = all.find { it.id == id }

    fun apply(context: Context, id: String): Boolean {
        val p = byId(id) ?: return false
        val scene: ListeningScene? = SceneLookup.byId(p.sceneId)
        SoftwareAnc.applyWithHardware(context, p.anc)
        p.maxVolumePct?.let { capVolume(context, it) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LAST, id).apply()
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
