package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

private const val PREFS = SceneAutomation.PREFS

private fun chipOn(context: Context, key: String) =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(key, false)

private fun setChip(context: Context, key: String, on: Boolean) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(key, on).apply()
}

private fun activate(context: Context, key: String, sceneId: String, anc: AncMode, profileId: String, toast: String) {
    val scene = SceneLookup.byId(sceneId)
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        .putBoolean(key, true)
        .putString("last_scene_id", scene?.id ?: sceneId)
        .putBoolean("pending_widget_scene", true)
        .apply()
    SoftwareAnc.applyWithHardware(context, anc)
    OneTapProfiles.apply(context, profileId)
    DspControlService.start(context)
    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
}

object PharmacyWaitOneTap {
    const val KEY_ON = "pharmacywait_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Apotheekwacht uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "pharmacywait", AncMode.AMBIENT, "pharmacywait", "Apotheekwacht · fluister + alert")
    }
}

object HomeExamOneTap {
    const val KEY_ON = "homeexam_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Thuis-examen uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "homeexam", AncMode.STRONG, "homeexam", "Thuis-examen · max focus")
    }
}

object BoatTripOneTap {
    const val KEY_ON = "boattrip_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Boottocht uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "boattrip", AncMode.WIND_GUARD, "boattrip", "Boottocht · wind + motor")
    }
}

object HybridMeetOneTap {
    const val KEY_ON = "hybridmeet_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Hybride-meet uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "hybridmeet", AncMode.AMBIENT, "hybridmeet", "Hybride-meet · stem helder")
    }
}
