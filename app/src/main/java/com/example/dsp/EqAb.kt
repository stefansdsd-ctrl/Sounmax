package com.example.dsp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** A/B-vergelijking van twee EQ-snapshots. */
object EqAb {
    private const val PREFS = "soundmax_eq_ab"
    private const val KEY_A = "a"
    private const val KEY_SIDE = "side"

    fun storeA(context: Context, dsp: AudioDspManager) {
        save(context, capture(dsp))
        setSide(context, "LIVE")
    }

    fun hasA(context: Context): Boolean = load(context) != null

    fun toggle(context: Context, dsp: AudioDspManager): String {
        val a = load(context) ?: return "geen A"
        val side = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_SIDE, "LIVE")
        return if (side == "A") {
            val b = loadB(context) ?: return "geen B"
            apply(dsp, b)
            setSide(context, "B")
            "B"
        } else {
            saveB(context, capture(dsp))
            apply(dsp, a)
            setSide(context, "A")
            "A"
        }
    }

    private data class Snap(val bands: List<Float>, val bass: Int, val virt: Int, val loud: Int, val clarity: Float)

    private fun capture(dsp: AudioDspManager) = Snap(
        dsp.bandGains.value,
        dsp.bassBoostStrength.value,
        dsp.virtualizerStrength.value,
        dsp.loudnessGain.value,
        dsp.clarityGain.value
    )

    private fun apply(dsp: AudioDspManager, snap: Snap) {
        snap.bands.forEachIndexed { i, g -> dsp.updateBandGain(i, g) }
        dsp.setBassBoost(snap.bass)
        dsp.setVirtualizer(snap.virt)
        dsp.setLoudness(snap.loud)
        dsp.setClarity(snap.clarity)
    }

    private fun toJson(s: Snap) = JSONObject().apply {
        put("b", JSONArray(s.bands))
        put("ba", s.bass)
        put("v", s.virt)
        put("l", s.loud)
        put("c", s.clarity.toDouble())
    }.toString()

    private fun fromJson(raw: String): Snap? = runCatching {
        val o = JSONObject(raw)
        val bands = o.getJSONArray("b")
        Snap(
            (0 until bands.length()).map { bands.getDouble(it).toFloat() },
            o.optInt("ba"),
            o.optInt("v"),
            o.optInt("l"),
            o.optDouble("c").toFloat()
        )
    }.getOrNull()

    private fun save(context: Context, snap: Snap) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_A, toJson(snap)).apply()
    }

    private fun saveB(context: Context, snap: Snap) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("b", toJson(snap)).apply()
    }

    private fun load(context: Context): Snap? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_A, null)?.let { fromJson(it) }

    private fun loadB(context: Context): Snap? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("b", null)?.let { fromJson(it) }

    private fun setSide(context: Context, side: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_SIDE, side).apply()
    }
}
