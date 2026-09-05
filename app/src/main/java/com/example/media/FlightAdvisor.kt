package com.example.media

import android.content.Context
import android.provider.Settings
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

object FlightAdvisor {
    const val KEY_ENABLED = "flight_advisor"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun isAirplane(context: Context): Boolean =
        Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context) || !isAirplane(context)) return scene
        return SceneLookup.byId("flightsleep") ?: SceneLookup.byId("quietcar") ?: scene
    }
}
