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

object PackedTramOneTap {
    const val KEY_ON = "packedtram_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Volle tram uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "packedtram", AncMode.ADAPTIVE, "packedtram", "Volle tram · drukte weg")
    }
}

object KidsBedOneTap {
    const val KEY_ON = "kidsbed_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Kids naar bed uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "kidsbed", AncMode.STRONG, "kidsbed", "Kids naar bed · fluister")
    }
}

object QuietCoachOneTap {
    const val KEY_ON = "quietcoach_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Stiltecoupé uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "quietcoach", AncMode.STRONG, "quietcoach", "Stiltecoupé+ · max rust")
    }
}

object DeliveryDoorOneTap {
    const val KEY_ON = "deliverydoor_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Thuisbezorgd uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "deliverydoor", AncMode.AMBIENT, "deliverydoor", "Thuisbezorgd · deurbel alert")
    }
}

object SchoolYardOneTap {
    const val KEY_ON = "schoolyard_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Schoolplein uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "schoolyard", AncMode.AMBIENT, "schoolyard", "Schoolplein · kids hoorbaar")
    }
}

object HospWaitOneTap {
    const val KEY_ON = "hospwait_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Ziekenhuiswacht uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "hospwait", AncMode.AMBIENT, "hospwait", "Ziekenhuiswacht · fluister")
    }
}
