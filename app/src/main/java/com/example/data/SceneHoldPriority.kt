package com.example.data

import android.content.Context

/** Eén volume-hold wint: ziekenhuis > warmte > lage accu > bedtime > overige holds. */
object SceneHoldPriority {
    const val HOSPITAL = "hospital"
    const val THERMAL = "thermal"
    const val LOWBATT = "lowbatt"
    const val BEDTIME = "bedtime"

    fun winner(context: Context): String? = when {
        HospitalHold.active(context) -> HOSPITAL
        ThermalHold.active(context) -> THERMAL
        LowBatteryHold.active(context) -> LOWBATT
        BedtimeFade.active(context) -> BEDTIME
        else -> null
    }

    fun applyExclusive(context: Context): Boolean {
        return when (winner(context)) {
            HOSPITAL -> HospitalHold.apply(context)
            THERMAL -> ThermalHold.apply(context)
            LOWBATT -> LowBatteryHold.apply(context)
            BEDTIME -> BedtimeFade.apply(context)
            else -> false
        }
    }

    fun label(context: Context): String = when (winner(context)) {
        HOSPITAL -> HospitalHold.label(context)
        THERMAL -> ThermalHold.label(context)
        LOWBATT -> LowBatteryHold.label(context)
        BEDTIME -> BedtimeFade.label(context)
        else -> "Geen hold"
    }
}
