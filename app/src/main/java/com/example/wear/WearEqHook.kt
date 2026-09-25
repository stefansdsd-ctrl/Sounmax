package com.example.wear

import android.content.Context
import com.example.dsp.EqShape162

object WearEqHook {
    fun tryHandle(context: Context, cmd: String): Boolean {
        if (!cmd.startsWith(WearPaths.CMD_EQ_PREFIX)) return false
        return EqShape162.applyNamed(context, cmd.removePrefix(WearPaths.CMD_EQ_PREFIX))
    }
}
