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

object DogWalkOneTap {
    const val KEY_ON = "dogwalk_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Hond uitlaten uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "hond", AncMode.WIND_GUARD, "dogwalk", "Hond uitlaten · wind + alert")
    }
}

object PharmacyOneTap {
    const val KEY_ON = "pharmacy_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Apotheek uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "apotheek", AncMode.AMBIENT, "pharmacy", "Apotheek · naam-oproep")
    }
}

object TerraceOneTap {
    const val KEY_ON = "terrace_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Terras uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "terrasavond", AncMode.WIND_GUARD, "terrace", "Terras · stem + wind")
    }
}

object RainBikeOneTap {
    const val KEY_ON = "rainbike_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Regenfiets uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "regenfiets", AncMode.WIND_GUARD, "rainbike", "Regenfiets · windfilter")
    }
}

object FerryOneTap {
    const val KEY_ON = "ferry_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Pont uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "pont", AncMode.STRONG, "ferry", "Pont · motor-ANC")
    }
}

object SilentWorkOneTap {
    const val KEY_ON = "silentwork_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Stiltewerk uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "stiltewerk", AncMode.STRONG, "silentwork", "Stiltewerk · diepe focus")
    }
}

object StadiumOneTap {
    const val KEY_ON = "stadium_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Stadion uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "stadionlive", AncMode.WIND_GUARD, "stadium", "Stadion · crowd + wind")
    }
}
