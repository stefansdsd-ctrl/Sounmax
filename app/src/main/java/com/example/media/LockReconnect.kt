package com.example.media

import android.content.Context
import com.example.data.SceneHistory
import com.example.dsp.SceneLookup

/** Lockscreen / widget: DSP + laatste scene + volume-ramp. */
object LockReconnect {
    fun run(context: Context): String {
        val scene = SceneLookup.byId(SceneHistory(context).current())
            ?: ReconnectScene.last(context)
        scene?.let {
            context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .edit()
                .putString("last_scene_id", it.id)
                .putBoolean("pending_widget_scene", true)
                .apply()
        }
        DspControlService.start(context)
        VolumeRamp.onReconnect(context)
        return scene?.let { "Herstel: ${it.name}" } ?: "DSP herstart"
    }
}
