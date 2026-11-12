package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * EQ Slot Picker: manage up to 5 named EQ presets for quick switching.
 * Stores bands + name in SharedPreferences.
 */
class EqSlotPicker(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("eq_slots", Context.MODE_PRIVATE)
    private val maxSlots = 5

    data class EqSlot(val name: String, val bands: List<Float>)

    fun saveSlot(index: Int, name: String, bands: List<Float>) {
        if (index !in 0 until maxSlots || bands.size != 10) return
        prefs.edit()
            .putString("slot_${index}_name", name)
            .putString("slot_${index}_bands", bands.joinToString(","))
            .apply()
    }

    fun loadSlot(index: Int): EqSlot? {
        if (index !in 0 until maxSlots) return null
        val name = prefs.getString("slot_${index}_name", null) ?: return null
        val bandsStr = prefs.getString("slot_${index}_bands", null) ?: return null
        val bands = bandsStr.split(",").mapNotNull { it.toFloatOrNull() }
        return if (bands.size == 10) EqSlot(name, bands) else null
    }

    fun listSlots(): List<EqSlot?> = (0 until maxSlots).map { loadSlot(it) }

    fun clearSlot(index: Int) {
        if (index !in 0 until maxSlots) return
        prefs.edit()
            .remove("slot_${index}_name")
            .remove("slot_${index}_bands")
            .apply()
    }
}
