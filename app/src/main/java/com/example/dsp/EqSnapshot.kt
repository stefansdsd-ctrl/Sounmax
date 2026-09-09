package com.example.dsp

import android.content.Context
import android.widget.Toast

/**
 * Bewaart twee EQ-snapshots (A/B) zodat je live kunt vergelijken.
 */
object EqSnapshot {
    private const val PREFS = "soundmax_eq_ab"
    private const val KEY_A = "gains_a"
    private const val KEY_B = "gains_b"
    private const val KEY_SIDE = "side"

    fun saveA(context: Context, gains: List<Float>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_A, gains.joinToString(",")).apply()
        Toast.makeText(context, "EQ A opgeslagen", Toast.LENGTH_SHORT).show()
    }

    fun saveB(context: Context, gains: List<Float>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_B, gains.joinToString(",")).apply()
        Toast.makeText(context, "EQ B opgeslagen", Toast.LENGTH_SHORT).show()
    }

    fun toggle(context: Context, current: List<Float>, apply: (List<Float>) -> Unit): String {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val a = parse(p.getString(KEY_A, null)) ?: current
        val b = parse(p.getString(KEY_B, null)) ?: current.map { 0f }
        val nextSide = if (p.getString(KEY_SIDE, "A") == "A") "B" else "A"
        apply(if (nextSide == "A") a else b)
        p.edit().putString(KEY_SIDE, nextSide).apply()
        val label = "EQ $nextSide"
        Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        return label
    }

    fun activeSide(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_SIDE, "A") ?: "A"

    private fun parse(raw: String?): List<Float>? {
        if (raw.isNullOrBlank()) return null
        val parts = raw.split(',').mapNotNull { it.toFloatOrNull() }
        return parts.takeIf { it.size >= 5 }
    }
}
