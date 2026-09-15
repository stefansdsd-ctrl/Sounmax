package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.ConversationBoost
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Home-chip Bel: talk-through + stem-EQ + volume-cap tot uit. */
object CallOneTap {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "call_chip_on"
    private const val KEY_BEFORE = "call_chip_before"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context) {
        if (isOn(context)) off(context) else on(context)
    }

    fun on(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val before = prefs.getString("last_scene_id", null)
        prefs.edit()
            .putBoolean(KEY_ON, true)
            .putString(KEY_BEFORE, before)
            .apply()
        TalkThrough.setEnabled(context, true)
        ConversationBoost.apply(true)
        SoftwareAnc.applyWithHardware(context, AncMode.AMBIENT)
        OneTapProfiles.apply(context, "talk")
        val scene = SceneLookup.byId("gesprek") ?: SceneLookup.byId("ambient")
        if (scene != null) {
            prefs.edit()
                .putString("last_scene_id", scene.id)
                .putBoolean("pending_widget_scene", true)
                .apply()
        }
        DspControlService.start(context)
        Toast.makeText(context, "Bel · talk-through + stem", Toast.LENGTH_SHORT).show()
    }

    fun off(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val before = prefs.getString(KEY_BEFORE, null)
        prefs.edit().putBoolean(KEY_ON, false).remove(KEY_BEFORE).apply()
        ConversationBoost.apply(false)
        TalkThrough.setEnabled(context, false)
        before?.let { id ->
            SceneLookup.byId(id)?.let {
                prefs.edit()
                    .putString("last_scene_id", it.id)
                    .putBoolean("pending_widget_scene", true)
                    .apply()
            }
        }
        DspControlService.start(context)
        Toast.makeText(context, "Bel uit · scene hersteld", Toast.LENGTH_SHORT).show()
    }
}
