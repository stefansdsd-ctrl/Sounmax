package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Eén volume-baseline voor alle ducks.
 * Alarm, bel, timer, TTS, nav, ping, assistent en mic bewaren niet meer elk hun eigen stand.
 * Laagste actieve cap wint. Herstel pas als niemand meer vasthoudt.
 */
object DuckLane {
    private const val PREFS = "sounmax_duck_lane"
    private const val STEP = 2

    fun hold(context: Context, owner: String, capPct: Int): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val p = prefs(context)
        if (p.getInt("base", -1) < 0) {
            p.edit().putInt("base", am.getStreamVolume(AudioManager.STREAM_MUSIC)).apply()
        }
        val owners = owners(p).toMutableSet()
        owners.add(owner)
        p.edit()
            .putString("owners", owners.joinToString(","))
            .putInt("cap_$owner", capPct)
            .apply()
        return stepToward(am, capVolume(context, am))
    }

    fun release(context: Context, owner: String): Boolean {
        val p = prefs(context)
        val owners = owners(p).toMutableSet()
        if (!owners.remove(owner) && p.getInt("base", -1) < 0) return false
        p.edit().putString("owners", owners.joinToString(",")).remove("cap_$owner").apply()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (owners.isNotEmpty()) return stepToward(am, capVolume(context, am))
        val baseVol = p.getInt("base", -1)
        if (baseVol < 0) return false
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur >= baseVol) {
            clear(context)
            return false
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur + STEP).coerceAtMost(baseVol), 0)
        if (cur + STEP >= baseVol) clear(context)
        return true
    }

    fun restoring(context: Context): Boolean =
        prefs(context).getInt("base", -1) >= 0 && owners(prefs(context)).isEmpty()

    fun heldBy(context: Context, owner: String) = owner in owners(prefs(context))

    private fun capVolume(context: Context, am: AudioManager): Int {
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val caps = owners(prefs(context)).map { prefs(context).getInt("cap_$it", 100) }
        val pct = caps.minOrNull() ?: 100
        return (max * pct) / 100
    }

    private fun stepToward(am: AudioManager, target: Int): Boolean {
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= target) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - STEP).coerceAtLeast(target), 0)
        return true
    }

    private fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun owners(p: android.content.SharedPreferences): Set<String> =
        p.getString("owners", "").orEmpty().split(",").filter { it.isNotBlank() }.toSet()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
