package com.example.wear

import android.content.Context
import com.example.data.BedtimeFade
import com.example.data.HospitalHold
import com.example.data.LowBatteryHold

object WearHoldCommands {
    fun tryHandle(context: Context, cmd: String): Boolean {
        when (cmd) {
            WearPaths.CMD_CYCLE_HOSPITAL -> HospitalHold.cycle(context)
            WearPaths.CMD_CYCLE_BEDTIME -> BedtimeFade.cycle(context)
            WearPaths.CMD_CYCLE_LOWBATT -> LowBatteryHold.cycle(context)
            else -> return false
        }
        WearBridge.publishStatus(context)
        return true
    }
}
