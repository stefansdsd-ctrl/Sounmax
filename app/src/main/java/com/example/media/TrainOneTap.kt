package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Trein: sterke ANC + volume-cap. */
object TrainOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "train_chip_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ON, false).apply()
            Toast.makeText(context, "Trein uit", Toast.LENGTH_SHORT).show()
            return
        }
        val scene = SceneLookup.byId("intercity")
            ?: SceneLookup.byId("quietcar")
            ?: SceneLookup.byId("commute")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putString("last_scene_id", scene?.id ?: "intercity")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "train")
        DspControlService.start(context)
        Toast.makeText(context, "Trein · max ANC", Toast.LENGTH_SHORT).show()
    }
}
