package com.example.dsp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Stack van max 5 EQ-snapshots voor undo + redo. */
object EqUndo {
    private const val PREFS = "soundmax_eq_undo"
    private const val KEY = "stack"
    private const val KEY_REDO = "redo"
    private const val MAX = 5

    data class Snap(
        val bands: List<Float>,
        val bass: Int,
        val virt: Int,
        val loud: Int,
        val clarity: Float
    )

    fun push(context: Context, dsp: AudioDspManager) {
        val stack = load(context, KEY).toMutableList()
        val next = capture(dsp)
        if (stack.lastOrNull() == next) return
        stack.add(next)
        while (stack.size > MAX) stack.removeAt(0)
        save(context, KEY, stack)
        save(context, KEY_REDO, emptyList())
    }

    fun depth(context: Context): Int = load(context, KEY).size
    fun redoDepth(context: Context): Int = load(context, KEY_REDO).size

    fun popApply(context: Context, dsp: AudioDspManager): Boolean {
        val stack = load(context, KEY).toMutableList()
        if (stack.isEmpty()) return false
        val current = capture(dsp)
        val snap = stack.removeAt(stack.lastIndex)
        save(context, KEY, stack)
        val redo = load(context, KEY_REDO).toMutableList()
        redo.add(current)
        while (redo.size > MAX) redo.removeAt(0)
        save(context, KEY_REDO, redo)
        apply(dsp, snap)
        return true
    }

    fun redoApply(context: Context, dsp: AudioDspManager): Boolean {
        val redo = load(context, KEY_REDO).toMutableList()
        if (redo.isEmpty()) return false
        val current = capture(dsp)
        val snap = redo.removeAt(redo.lastIndex)
        save(context, KEY_REDO, redo)
        val stack = load(context, KEY).toMutableList()
        stack.add(current)
        while (stack.size > MAX) stack.removeAt(0)
        save(context, KEY, stack)
        apply(dsp, snap)
        return true
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY).remove(KEY_REDO).apply()
    }

    private fun capture(dsp: AudioDspManager) = Snap(
        bands = dsp.bandGains.value,
        bass = dsp.bassBoostStrength.value,
        virt = dsp.virtualizerStrength.value,
        loud = dsp.loudnessGain.value,
        clarity = dsp.clarityGain.value
    )

    private fun apply(dsp: AudioDspManager, snap: Snap) {
        snap.bands.forEachIndexed { i, g -> dsp.updateBandGain(i, g) }
        dsp.setBassBoost(snap.bass)
        dsp.setVirtualizer(snap.virt)
        dsp.setLoudness(snap.loud)
        dsp.setClarity(snap.clarity)
    }

    private fun load(context: Context, key: String): List<Snap> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key, null)
            ?: return emptyList()
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

    private fun save(context: Context, key: String, stack: List<Snap>) {
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
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
