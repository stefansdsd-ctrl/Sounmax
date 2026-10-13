package com.example.data

import android.content.Context
import com.example.dsp.EqPreset
import org.json.JSONArray
import org.json.JSONObject

/**
 * Multi-slot opslaan/laden custom 10-band EQ curves via SharedPreferences.
 * Slots 0-4. Auto-apply slot 0 on connect.
 */
object EqPersist {
    private const val PREFS = "soundmax_eq_persist"
    private const val KEY_SLOTS = "eq_slots_json"
    private const val MAX_SLOTS = 5

    fun save(context: Context, preset: EqPreset, slot: Int = 0) {
        if (slot !in 0 until MAX_SLOTS) return
        val slots = loadAll(context).toMutableList()
        while (slots.size <= slot) slots.add(null)
        slots[slot] = preset
        saveAll(context, slots)
    }

    fun load(context: Context, slot: Int = 0): EqPreset? {
        val slots = loadAll(context)
        return slots.getOrNull(slot)
    }

    fun hasSaved(context: Context, slot: Int = 0): Boolean = load(context, slot) != null

    fun loadAll(context: Context): List<EqPreset?> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SLOTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.optJSONObject(i) ?: return@map null
                val bands = obj.optJSONArray("bands")?.let { ja ->
                    (0 until ja.length()).map { ja.optDouble(it).toFloat() }
                } ?: emptyList()
                if (bands.size != 10) null else EqPreset(
                    name = obj.optString("name", "Custom $i"),
                    bandGains = bands,
                    bassBoost = obj.optInt("bass", 0),
                    virtualizer = obj.optInt("virt", 0),
                    loudness = obj.optInt("loud", 0),
                    clarity = obj.optDouble("clarity", 0.0).toFloat(),
                    isCustom = true,
                    category = "Persist",
                    description = "Slot $i"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveAll(context: Context, slots: List<EqPreset?>) {
        val arr = JSONArray()
        slots.forEach { p ->
            if (p == null) {
                arr.put(JSONObject.NULL)
            } else {
                val obj = JSONObject()
                obj.put("name", p.name)
                obj.put("bands", JSONArray(p.bandGains))
                obj.put("bass", p.bassBoost)
                obj.put("virt", p.virtualizer)
                obj.put("loud", p.loudness)
                obj.put("clarity", p.clarity.toDouble())
                arr.put(obj)
            }
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_SLOTS, arr.toString())
            .apply()
    }

    fun clear(context: Context, slot: Int) {
        if (slot !in 0 until MAX_SLOTS) return
        val slots = loadAll(context).toMutableList()
        if (slot < slots.size) slots[slot] = null
        saveAll(context, slots)
    }
}
