package com.example.dsp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Stack van max 5 EQ-snapshots voor één-tik undo. */
object EqUndo {
    private const val PREFS = "soundmax_eq_undo"
    private const val KEY = "stack"
    private const val MAX = 5

    data class Snap(
        val bands: List<Float>,
        val bass: Int,
        val virt: Int,
        val loud: Int,
        val clarity: Float
    )

    fun push(context: Context, dsp: AudioDspManager) {
        val stack = load(context).toMutableList()
        val next = Snap(
            bands = dsp.bandGains.value,
            bass = dsp.bassBoostStrength.value,
            virt = dsp.virtualizerStrength.value,
            loud = dsp.loudnessGain.value,
            clarity = dsp.clarityGain.value
        )
        if (stack.lastOrNull() == next) return
        stack.add(next)
        while (stack.size > MAX) stack.removeAt(0)
        save(context, stack)
    }

    fun depth(context: Context): Int = load(context).size

    fun popApply(context: Context, dsp: AudioDspManager): Boolean {
        val stack = load(context).toMutableList()
        if (stack.isEmpty()) return false
        val snap = stack.removeAt(stack.lastIndex)
        save(context, stack)
        snap.bands.forEachIndexed { i, g -> dsp.updateBandGain(i, g) }
        dsp.setBassBoost(snap.bass)
        dsp.setVirtualizer(snap.virt)
        dsp.setLoudness(snap.loud)
        dsp.setClarity(snap.clarity)
        return true
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY).apply()
    }

    private fun load(context: Context): List<Snap> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val bands = o.getJSONArray("b")
                Snap(
                    bands = (0 until bands.length()).map { bands.getDouble(it).toFloat() },
                    bass = o.optInt("ba"),
                    virt = o.optInt("v"),
                    loud = o.optInt("l"),
                    clarity = o.optDouble("c").toFloat()
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun save(context: Context, stack: List<Snap>) {
        val arr = JSONArray()
        stack.forEach { s ->
            arr.put(JSONObject().apply {
                put("b", JSONArray(s.bands))
                put("ba", s.bass)
                put("v", s.virt)
                put("l", s.loud)
                put("c", s.clarity.toDouble())
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
}
