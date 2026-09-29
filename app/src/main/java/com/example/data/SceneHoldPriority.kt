package com.example.data

import android.content.Context

/** Eén volume-hold wint: ziekenhuis > bedtime > overige holds. */
object SceneHoldPriority {
    const val HOSPITAL = "hospital"
    const val BEDTIME = "bedtime"

    fun winner(context: Context): String? = when {
        HospitalHold.active(context) -> HOSPITAL
        BedtimeFade.active(context) -> BEDTIME
        else -> null
    }

    fun applyExclusive(context: Context): Boolean {
        return when (winner(context)) {
            HOSPITAL -> HospitalHold.apply(context)
            BEDTIME -> BedtimeFade.apply(context)
            else -> false
        }
    }
}
