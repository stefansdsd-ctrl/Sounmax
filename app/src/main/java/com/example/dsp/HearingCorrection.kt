package com.example.dsp

import android.content.Context
import com.example.data.HearingProfileEntity
import com.example.data.SoundMaxDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class HearingEar { BOTH, LEFT, RIGHT }

object HearingCorrection {
    private const val PREFS = "soundmax_wellness"
    const val KEY_AUTO = "auto_hearing"
    const val KEY_PENDING = "pending_hearing_apply"
    const val KEY_EAR = "hearing_ear"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_AUTO, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_AUTO, on).apply()
    }

    fun savedEar(context: Context): HearingEar =
        runCatching { HearingEar.valueOf(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_EAR, HearingEar.BOTH.name)!!) }
            .getOrDefault(HearingEar.BOTH)

    fun setEar(context: Context, ear: HearingEar) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_EAR, ear.name).apply()
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

    fun toPreset(profile: HearingProfileEntity, ear: HearingEar = HearingEar.BOTH): Pair<EqPreset, Int> {
        val leftGains = profile.leftGains.split(",").mapNotNull { it.toFloatOrNull() }
        val rightGains = profile.rightGains.split(",").mapNotNull { it.toFloatOrNull() }
        fun pick(i: Int): Float {
            val l = leftGains.getOrElse(i) { 0f }
            val r = rightGains.getOrElse(i) { l }
            val v = when (ear) {
                HearingEar.LEFT -> l
                HearingEar.RIGHT -> r
                HearingEar.BOTH -> (l + r) / 2f
            }
            return v.coerceIn(-6f, 8f)
        }
        val tenBands = listOf(
            pick(0), pick(0), pick(1), pick(2), pick(3),
            pick(4), pick(5), pick(5), pick(6), pick(6)
        )
        val imbalance = run {
            val l = leftGains.average().takeIf { !it.isNaN() } ?: 0.0
            val r = rightGains.average().takeIf { !it.isNaN() } ?: 0.0
            ((r - l) * 8).toInt().coerceIn(-40, 40)
        }
        val balance = when (ear) {
            HearingEar.LEFT -> -28 + imbalance / 2
            HearingEar.RIGHT -> 28 + imbalance / 2
            HearingEar.BOTH -> imbalance
        }.coerceIn(-80, 80)
        val label = when (ear) {
            HearingEar.LEFT -> "L"
            HearingEar.RIGHT -> "R"
            HearingEar.BOTH -> "L+R"
        }
        val preset = EqPreset(
            name = "Gehoor $label",
            bandGains = tenBands,
            bassBoost = 250,
            virtualizer = 200,
            loudness = 200,
            clarity = 4.5f,
            isCustom = true,
            description = "Per-oor correctie ($label) uit gehoortest, balans $balance."
        )
        return preset to balance
    }

    suspend fun loadLatest(context: Context): HearingProfileEntity? = withContext(Dispatchers.IO) {
        SoundMaxDatabase.getDatabase(context).hearingProfileDao().getLatestOnce()
    }

    fun channelGains(profile: HearingProfileEntity): Pair<List<Float>, List<Float>> {
        val leftGains = profile.leftGains.split(",").mapNotNull { it.toFloatOrNull() }
        val rightGains = profile.rightGains.split(",").mapNotNull { it.toFloatOrNull() }
        fun ten(src: List<Float>): List<Float> {
            fun p(i: Int) = src.getOrElse(i) { 0f }.coerceIn(-6f, 8f)
            return listOf(p(0), p(0), p(1), p(2), p(3), p(4), p(5), p(5), p(6), p(6))
        }
        return ten(leftGains) to ten(rightGains)
    }

    fun apply(dsp: AudioDspManager, profile: HearingProfileEntity, ear: HearingEar = HearingEar.BOTH) {
        val (preset, balance) = toPreset(profile, ear)
        dsp.setBalance(balance)
        dsp.applyPreset(preset)
        StereoDynamics.init()
        val (l, r) = channelGains(profile)
        val mute = List(10) { -12f }
        when (ear) {
            HearingEar.BOTH -> StereoDynamics.applyBands(l, r)
            HearingEar.LEFT -> StereoDynamics.applyBands(l, mute)
            HearingEar.RIGHT -> StereoDynamics.applyBands(mute, r)
        }
    }
}
