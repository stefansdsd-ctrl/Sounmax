package com.example.dsp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Drie vaste EQ-slots: Werk, Thuis, Sport. */
object NamedEqSlots {
    val NAMES = listOf("Werk", "Thuis", "Sport")
    private const val PREFS = "soundmax_eq_slots"

    fun save(context: Context, slot: Int, dsp: AudioDspManager) {
        if (slot !in NAMES.indices) return
        val o = JSONObject().apply {
            put("b", JSONArray(dsp.bandGains.value))
            put("ba", dsp.bassBoostStrength.value)
            put("v", dsp.virtualizerStrength.value)
            put("l", dsp.loudnessGain.value)
            put("c", dsp.clarityGain.value.toDouble())
            put("t", System.currentTimeMillis())
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString("s$slot", o.toString()).apply()
    }

    fun has(context: Context, slot: Int): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).contains("s$slot")

    fun clear(context: Context, slot: Int) {
        if (slot !in NAMES.indices) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove("s$slot").apply()
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun apply(context: Context, slot: Int, dsp: AudioDspManager): Boolean {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("s$slot", null)
            ?: return false
        return runCatching {
            val o = JSONObject(raw)
            val bands = o.getJSONArray("b")
            (0 until bands.length()).forEach { i ->
                dsp.updateBandGain(i, bands.getDouble(i).toFloat())
            }
            dsp.setBassBoost(o.optInt("ba"))
            dsp.setVirtualizer(o.optInt("v"))
            dsp.setLoudness(o.optInt("l"))
            dsp.setClarity(o.optDouble("c").toFloat())
            true
        }.getOrDefault(false)
    }

    fun bands(context: Context, slot: Int): List<Float>? {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("s$slot", null)
            ?: return null
        return runCatching {
            val o = JSONObject(raw)
            val bands = o.getJSONArray("b")
            (0 until bands.length()).map { bands.getDouble(it).toFloat() }
        }.getOrNull()
    }
}
