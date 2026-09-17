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

object TramOneTap {
    const val KEY_ON = "tram_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Tram uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "tram", AncMode.ADAPTIVE, "tram", "Tram · omroep + ANC")
    }
}

object BakeryOneTap {
    const val KEY_ON = "bakery_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Bakker uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "bakker", AncMode.AMBIENT, "bakery", "Bakker · naam-oproep")
    }
}

object ParkingOneTap {
    const val KEY_ON = "parking_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Parkeergarage uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "parkeergarage", AncMode.STRONG, "parking", "Parkeergarage · echo-ANC")
    }
}

object ExamOneTap {
    const val KEY_ON = "exam_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Tentamen uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "tentamen", AncMode.STRONG, "exam", "Tentamen · stil + focus")
    }
}

object LaundryOneTap {
    const val KEY_ON = "laundry_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Wasruimte uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "wasruimte", AncMode.STRONG, "laundry", "Wasruimte · machine-ANC")
    }
}

object PrivacyOvOneTap {
    const val KEY_ON = "privacyov_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Privacy-OV uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "privacyov", AncMode.STRONG, "privacyov", "Privacy-OV · lekdicht + cap 45%")
    }
}
