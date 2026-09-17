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

object StormOneTap {
    const val KEY_ON = "storm_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Storm uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "storm", AncMode.STRONG, "storm", "Storm · ANC + wind")
    }
}

object FairOneTap {
    const val KEY_ON = "kermis_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Kermis uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "kermis", AncMode.WIND_GUARD, "kermis", "Kermis · wind + crowd")
    }
}

object PartnerSleepOneTap {
    const val KEY_ON = "partnerslaap_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Partner slaapt uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "partnerslaap", AncMode.AMBIENT, "partnerslaap", "Partner slaapt · fluister")
    }
}

object LongCallOneTap {
    const val KEY_ON = "langecall_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Lange call uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "langecall", AncMode.AMBIENT, "langecall", "Lange call · stemhelder")
    }
}
