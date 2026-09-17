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

object NightBusOneTap {
    const val KEY_ON = "nachtbus_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Nachtbus uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "nachtbus", AncMode.STRONG, "nachtbus", "Nachtbus · ANC + omroep")
    }
}

object NeighborPartyOneTap {
    const val KEY_ON = "burenfeest_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Burenfeest uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "burenfeest", AncMode.STRONG, "burenfeest", "Burenfeest · muur-ANC")
    }
}

object LectureEchoOneTap {
    const val KEY_ON = "collegeecho_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "College-echo uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "college-echo", AncMode.AMBIENT, "collegeecho", "College-echo · stemhelder")
    }
}

object LeakCheckOneTap {
    const val KEY_ON = "lekcheck_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Lekcheck uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "lekcheck", AncMode.OFF, "lekcheck", "Lekcheck · 35% + flat")
    }
}
