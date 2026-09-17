package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.dsp.AncMode
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

object OneTapProfiles {
    const val PREFS = "soundmax_prefs"
    const val KEY_LAST = "one_tap_last"
    private const val KEY_ORDER = "one_tap_recent_csv"

    data class Profile(
        val id: String,
        val label: String,
        val sceneId: String,
        val anc: AncMode,
        val maxVolumePct: Int?,
        val group: String = "Overig"
    )

    val all = listOf(
        Profile("commute", "Woon-werk", "commute", AncMode.STRONG, 80, "Onderweg"),
        Profile("train", "Trein", "intercity", AncMode.STRONG, 75, "Onderweg"),
        Profile("plane", "Vliegtuig", "intercity", AncMode.STRONG, 65, "Onderweg"),
        Profile("bike", "Fiets", "avondfiets", AncMode.WIND_GUARD, 70, "Onderweg"),
        Profile("outdoor", "Buiten", "outdoor", AncMode.WIND_GUARD, 70, "Onderweg"),
        Profile("rain", "Regen", "outdoor", AncMode.WIND_GUARD, 70, "Onderweg"),
        Profile("focus", "Focus", "focus", AncMode.STRONG, null, "Werk"),
        Profile("school", "School", "schoolochtend", AncMode.AMBIENT, 55, "Werk"),
        Profile("lib", "Bieb", "library", AncMode.STRONG, 40, "Werk"),
        Profile("gym", "Sport", "sportschool", AncMode.ADAPTIVE, 85, "Sport"),
        Profile("game", "Game", "party", AncMode.OFF, 80, "Sport"),
        Profile("walk", "Wandelen", "outdoor", AncMode.WIND_GUARD, 70, "Sport"),
        Profile("podcast", "Podcast", "library", AncMode.ADAPTIVE, 70, "Media"),
        Profile("kitchen", "Koken", "cook", AncMode.AMBIENT, 60, "Dag"),
        Profile("movie", "Film", "film", AncMode.STRONG, 75, "Media"),
        Profile("sleep", "Slaap", "sleep", AncMode.ADAPTIVE, 40, "Nacht"),
        Profile("home", "Thuis", "thuisavond", AncMode.OFF, 55, "Nacht"),
        Profile("talk", "Gesprek", "ambient", AncMode.AMBIENT, null, "Dag"),
        Profile("cafe", "Café", "koffietent", AncMode.AMBIENT, 60, "Dag"),
        Profile("shop", "Winkelen", "mall", AncMode.AMBIENT, 60, "Dag"),
        Profile("kids", "Kids", "thuiskids", AncMode.AMBIENT, 45, "Dag"),
        Profile("car", "Auto", "car", AncMode.WIND_GUARD, 70, "Onderweg"),
        Profile("metro", "Metro", "metro", AncMode.STRONG, 70, "Onderweg"),
        Profile("concert", "Concert", "concert", AncMode.ADAPTIVE, 80, "Media"),
        Profile("restaurant", "Eten", "restaurant", AncMode.AMBIENT, 55, "Dag"),
        Profile("college", "College", "college", AncMode.AMBIENT, 50, "Werk"),
        Profile("museum", "Museum", "museum", AncMode.OFF, 40, "Dag"),
        Profile("build", "Bouw", "bouw", AncMode.STRONG, 70, "Onderweg"),
        Profile("beach", "Strand", "strand", AncMode.WIND_GUARD, 55, "Sport"),
        Profile("zoom", "Videocall", "zoom", AncMode.AMBIENT, 50, "Werk"),
        Profile("yoga", "Yoga", "yoga", AncMode.OFF, 40, "Sport"),
        Profile("bus", "Bus", "bus", AncMode.ADAPTIVE, 70, "Onderweg"),
        Profile("market", "AH", "supermarkt", AncMode.AMBIENT, 55, "Dag"),
        Profile("wfh", "WFH", "thuiswerk", AncMode.ADAPTIVE, 55, "Werk"),
        Profile("garden", "Tuin", "tuin", AncMode.WIND_GUARD, 60, "Sport"),
        Profile("pool", "Zwembad", "zwembad", AncMode.WIND_GUARD, 50, "Sport"),
        Profile("sauna", "Sauna", "sauna", AncMode.OFF, 35, "Sport"),
        Profile("run", "Hardlopen", "hardlopen", AncMode.WIND_GUARD, 70, "Sport"),
        Profile("clinic", "Huisarts", "huisarts", AncMode.AMBIENT, 40, "Dag"),
        Profile("traffic", "File", "file", AncMode.STRONG, 70, "Onderweg"),
        Profile("ikea", "IKEA", "ikea", AncMode.AMBIENT, 50, "Dag"),
        Profile("salon", "Kapper", "kapper", AncMode.AMBIENT, 45, "Dag"),
        Profile("ns", "NS-storing", "nsstoring", AncMode.STRONG, 65, "Onderweg"),
        Profile("baby", "Baby", "babyslaap", AncMode.AMBIENT, 30, "Nacht"),
        Profile("lowbatt", "Accu laag", "acculaag", AncMode.OFF, 40, "Overig"),
        Profile("tram", "Tram", "tram", AncMode.ADAPTIVE, 65, "Onderweg"),
        Profile("bakery", "Bakker", "bakker", AncMode.AMBIENT, 45, "Dag"),
        Profile("parking", "Parkeer", "parkeergarage", AncMode.STRONG, 65, "Onderweg"),
        Profile("exam", "Tentamen", "tentamen", AncMode.STRONG, 35, "Werk"),
        Profile("laundry", "Wasruimte", "wasruimte", AncMode.STRONG, 60, "Dag"),
        Profile("privacyov", "Privacy-OV", "privacyov", AncMode.STRONG, 45, "Onderweg"),
        Profile("off", "Uit", "default", AncMode.OFF, null, "Overig")
    )

    val GROUP_ORDER = listOf("Onderweg", "Werk", "Sport", "Media", "Nacht", "Dag", "Overig")

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
