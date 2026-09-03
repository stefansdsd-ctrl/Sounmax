package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.dsp.LdacQualityMode

/**
 * Lage headset-accu: minder ANC/LDAC zodat de koptelefoon langer meegaat.
 */
object BatteryPowerAdvisor {
    fun adjust(context: Context, scene: ListeningScene, batteryPercent: Int?): ListeningScene {
        if (batteryPercent == null) return scene
        if (batteryPercent <= 15) {
            return ListeningScenes.byId("saver") ?: scene.copy(
                description = "${scene.description} · accubesparing ${batteryPercent}%",
                preferredLdac = LdacQualityMode.CONNECTION_330
            )
        }
        if (batteryPercent <= 25 && scene.ancMode.name == "STRONG") {
            return scene.copy(
                description = "${scene.description} · accu ${batteryPercent}%",
                preferredLdac = LdacQualityMode.CONNECTION_330
            )
        }
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt("last_battery", batteryPercent)
            .apply()
        return scene
    }
}
