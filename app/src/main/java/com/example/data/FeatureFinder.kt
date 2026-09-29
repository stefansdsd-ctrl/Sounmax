package com.example.data

import android.content.Context

/** Zoek holds/scenes op trefwoord. */
object FeatureFinder {
    data class Hit(val id: String, val title: String, val hint: String)

    private val catalog = listOf(
        Hit("thermal", "Warmte-hold", "volume-cap bij ≥40°C"),
        Hit("hospital", "Ziekenhuis-hold", "stil + prioriteit 1"),
        Hit("lowbatt", "Lage-accu-hold", "DSP-spaarstand"),
        Hit("bedtime", "Bedtime-fade", "nachtvolume"),
        Hit("bt", "BT-herstel", "scene+volume na reconnect"),
        Hit("dose", "Gehoorcap", "weekdosis WHO"),
        Hit("walk", "Wandel-hold", "buiten + veiligheid"),
        Hit("bike", "Fiets-hold", "omgevingsgeluid"),
        Hit("drive", "Rit-hold", "auto"),
        Hit("wind", "Wind-hold", "ruis dempen"),
        Hit("rain", "Regen-hold", "weer"),
        Hit("focus", "Focus", "DND + EQ"),
        Hit("sleep", "Slaap", "timer + fade"),
    )

    fun search(query: String): List<Hit> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return catalog
        return catalog.filter {
            it.id.contains(q) || it.title.lowercase().contains(q) || it.hint.contains(q)
        }
    }

    fun label(context: Context, id: String): String = when (id) {
        "thermal" -> ThermalHold.label(context)
        "hospital" -> HospitalHold.label(context)
        "lowbatt" -> LowBatteryHold.label(context)
        "bedtime" -> BedtimeFade.label(context)
        "bt" -> BtReconnectRestore.label(context)
        else -> catalog.firstOrNull { it.id == id }?.title ?: id
    }
}
