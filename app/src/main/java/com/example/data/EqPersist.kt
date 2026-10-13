package com.example.data

import android.content.Context
import android.content.SharedPreferences

object EqPersist {
    private const val PREFS = "soundmax_eq"
    private const val KEY_CURVE = "custom_curve"
    private const val BANDS = 10

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(context: Context, bands: FloatArray) {
        if (bands.size != BANDS) return
        val serialized = bands.joinToString(",") { it.toString() }
        prefs(context).edit().putString(KEY_CURVE, serialized).apply()
    }

    fun load(context: Context): FloatArray? {
        val raw = prefs(context).getString(KEY_CURVE, null) ?: return null
        val parts = raw.split(",")
        if (parts.size != BANDS) return null
        return parts.mapNotNull { it.toFloatOrNull() }.toFloatArray().takeIf { it.size == BANDS }
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_CURVE).apply()
    }
}
