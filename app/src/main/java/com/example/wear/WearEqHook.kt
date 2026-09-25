package com.example.wear

import android.content.Context
import com.example.dsp.EqShape164

object WearEqHook {
    fun tryHandle(context: Context, cmd: String): Boolean {
        if (!cmd.startsWith(WearPaths.CMD_EQ_PREFIX)) return false
        return EqShape164.applyNamed(context, cmd.removePrefix(WearPaths.CMD_EQ_PREFIX))
    }
}
