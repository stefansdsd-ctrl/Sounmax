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

object NeighborhoodOneTap {
    const val KEY_ON = "neighborhood_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Buurtlawaai uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "neighborhood", AncMode.STRONG, "neighborhood", "Buurtlawaai · ANC max")
    }
}

object CookEveOneTap {
    const val KEY_ON = "cookeve_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Avondkook uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "cookeve", AncMode.AMBIENT, "cookeve", "Avondkook · ambient")
    }
}

object InboxZeroOneTap {
    const val KEY_ON = "inboxzero_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Inbox-sprint uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "inboxzero", AncMode.STRONG, "inboxzero", "Inbox-sprint · 45 min focus")
    }
}

object PlatformWaitOneTap {
    const val KEY_ON = "platformwait_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Perronwacht uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "platformwait", AncMode.WIND_GUARD, "platformwait", "Perronwacht · omroep + wind")
    }
}
