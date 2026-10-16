package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Simpele history van headphone battery levels.
 * Bewaart laatste 50 metingen (level + timestamp).
 */
object BatteryHistory {
    private const val PREFS = "sounmax_bat_history"
    private const val KEY = "history_json"
    private const val MAX = 50

    fun record(context: Context, level: Int) {
        if (level !in 0..100) return
        val list = load(context).toMutableList()
        list.add(Entry(level, System.currentTimeMillis()))
        if (list.size > MAX) list.removeAt(0)
        save(context, list)
    }

    fun load(context: Context): List<Entry> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                Entry(obj.optInt("l", -1), obj.optLong("t", 0))
            }.filter { it.level in 0..100 }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun averageLast(context: Context, n: Int = 5): Float {
        val recent = load(context).takeLast(n)
        return if (recent.isEmpty()) -1f else recent.map { it.level }.average().toFloat()
    }

    private fun save(context: Context, entries: List<Entry>) {
        val arr = JSONArray()
        entries.forEach {
            arr.put(JSONObject().put("l", it.level).put("t", it.timestamp))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, arr.toString()).apply()
    }

    data class Entry(val level: Int, val timestamp: Long)
}
