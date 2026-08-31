package com.example.dsp

import android.content.Context
import com.example.data.HearingProfileEntity
import com.example.data.SoundMaxDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HearingCorrection {
    const val PREFS = "soundmax_wellness"
    const val KEY_AUTO = "auto_hearing"
    const val KEY_PENDING = "pending_hearing_apply"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_AUTO, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_AUTO, on).apply()
    }

    fun markPending(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_PENDING, true).apply()
    }

    fun consumePending(context: Context): Boolean {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val pending = p.getBoolean(KEY_PENDING, false)
        if (pending) p.edit().putBoolean(KEY_PENDING, false).apply()
        return pending
    }

    fun toPreset(profile: HearingProfileEntity): Pair<EqPreset, Int> {
        val leftGains = profile.leftGains.split(",").mapNotNull { it.toFloatOrNull() }
        val rightGains = profile.rightGains.split(",").mapNotNull { it.toFloatOrNull() }
        fun avg(i: Int): Float {
            val l = leftGains.getOrElse(i) { 0f }
            val r = rightGains.getOrElse(i) { l }
            return ((l + r) / 2f).coerceIn(-6f, 8f)
        }
        val tenBands = listOf(
            avg(0), avg(0), avg(1), avg(2), avg(3),
            avg(4), avg(5), avg(5), avg(6), avg(6)
        )
        val imbalance = run {
            val l = leftGains.average().takeIf { !it.isNaN() } ?: 0.0
            val r = rightGains.average().takeIf { !it.isNaN() } ?: 0.0
            ((r - l) * 8).toInt().coerceIn(-20, 20)
        }
        val preset = EqPreset(
            name = "Gepersonaliseerde Gehoorcompensatie",
            bandGains = tenBands,
            bassBoost = 250,
            virtualizer = 200,
            loudness = 200,
            clarity = 4.5f,
            isCustom = true,
            description = "L+R gemiddelde uit gehoortest, met lichte balanscorrectie."
        )
        return preset to (50 + imbalance)
    }

    suspend fun loadLatest(context: Context): HearingProfileEntity? = withContext(Dispatchers.IO) {
        SoundMaxDatabase.getDatabase(context).hearingProfileDao().getLatestOnce()
    }

    fun apply(dsp: AudioDspManager, profile: HearingProfileEntity) {
        val (preset, balance) = toPreset(profile)
        if (balance != 50) dsp.setBalance(balance)
        dsp.applyPreset(preset)
    }
}
