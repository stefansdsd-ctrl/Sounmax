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

object CollegeOneTap {
    const val KEY_ON = "college_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "College uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "college", AncMode.AMBIENT, "college", "College · stem + ambient")
    }
}

object MuseumOneTap {
    const val KEY_ON = "museum_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Museum uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "museum", AncMode.OFF, "museum", "Museum · zacht + ruim")
    }
}

object BuildOneTap {
    const val KEY_ON = "build_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Bouw uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "bouw", AncMode.STRONG, "build", "Bouw · max ANC")
    }
}

object BeachOneTap {
    const val KEY_ON = "beach_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Strand uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "strand", AncMode.WIND_GUARD, "beach", "Strand · windfilter")
    }
}

object ZoomOneTap {
    const val KEY_ON = "zoom_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Videocall uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "zoom", AncMode.AMBIENT, "zoom", "Videocall · stemhelder")
    }
}

object YogaOneTap {
    const val KEY_ON = "yoga_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Yoga uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "yoga", AncMode.OFF, "yoga", "Yoga · zacht + ruim")
    }
}

object BusOneTap {
    const val KEY_ON = "bus_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Bus uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "bus", AncMode.ADAPTIVE, "bus", "Bus · ANC + omroep")
    }
}

object MarketOneTap {
    const val KEY_ON = "market_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Supermarkt uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "supermarkt", AncMode.AMBIENT, "market", "Supermarkt · ambient")
    }
}

object WfhOneTap {
    const val KEY_ON = "wfh_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Thuiswerk uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "thuiswerk", AncMode.ADAPTIVE, "wfh", "Thuiswerk · focus")
    }
}

object GardenOneTap {
    const val KEY_ON = "garden_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Tuin uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "tuin", AncMode.WIND_GUARD, "garden", "Tuin · windfilter")
    }
}

object PoolOneTap {
    const val KEY_ON = "pool_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Zwembad uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "zwembad", AncMode.WIND_GUARD, "pool", "Zwembad · wind + zacht")
    }
}

object SaunaOneTap {
    const val KEY_ON = "sauna_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Sauna uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "sauna", AncMode.OFF, "sauna", "Sauna · zacht + stil")
    }
}

object RunOneTap {
    const val KEY_ON = "run_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Hardlopen uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "hardlopen", AncMode.WIND_GUARD, "run", "Hardlopen · windfilter")
    }
}

object ClinicOneTap {
    const val KEY_ON = "clinic_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Huisarts uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "huisarts", AncMode.AMBIENT, "clinic", "Huisarts · zacht ambient")
    }
}

object TrafficOneTap {
    const val KEY_ON = "traffic_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "File uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "file", AncMode.STRONG, "traffic", "File · max ANC")
    }
}

object IkeaOneTap {
    const val KEY_ON = "ikea_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "IKEA uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "ikea", AncMode.AMBIENT, "ikea", "IKEA · ambient + stem")
    }
}

object SalonOneTap {
    const val KEY_ON = "salon_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Kapper uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "kapper", AncMode.AMBIENT, "salon", "Kapper · ambient")
    }
}

object NsOneTap {
    const val KEY_ON = "ns_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "NS-storing uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "nsstoring", AncMode.STRONG, "ns", "NS-storing · max ANC")
    }
}

object BabyOneTap {
    const val KEY_ON = "baby_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Baby-modus uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "babyslaap", AncMode.AMBIENT, "baby", "Baby slaapt · zacht + laag volume")
    }
}

object LowBattOneTap {
    const val KEY_ON = "lowbatt_chip_on"
    fun isOn(c: Context) = chipOn(c, KEY_ON)
    fun toggle(c: Context) {
        if (isOn(c)) { setChip(c, KEY_ON, false); Toast.makeText(c, "Accu-laag modus uit", Toast.LENGTH_SHORT).show(); return }
        activate(c, KEY_ON, "acculaag", AncMode.OFF, "lowbatt", "Accu laag · ANC uit + volume-cap")
    }
}

object OffOneTap {
    private val CHIP_KEYS = listOf(
        "call_chip_on", "podcast_chip_on", "flight_chip_on", "game_chip_on", "movie_chip_on",
        "rain_chip_on", "kitchen_chip_on", "walk_chip_on", "focus_chip_on", "gym_chip_on",
        "sleep_chip_on", "commute_chip_on", "office_chip_on", "library_chip_on", "cafe_chip_on",
        "meeting_chip_on", "bike_chip_on", "shop_chip_on", "kids_chip_on", "train_chip_on",
        "car_chip_on", "metro_chip_on", "concert_chip_on", "restaurant_chip_on",
        CollegeOneTap.KEY_ON, MuseumOneTap.KEY_ON, BuildOneTap.KEY_ON,
        BeachOneTap.KEY_ON, ZoomOneTap.KEY_ON, YogaOneTap.KEY_ON,
        BusOneTap.KEY_ON, MarketOneTap.KEY_ON, WfhOneTap.KEY_ON,
        GardenOneTap.KEY_ON, PoolOneTap.KEY_ON, SaunaOneTap.KEY_ON,
        RunOneTap.KEY_ON, ClinicOneTap.KEY_ON, TrafficOneTap.KEY_ON,
        IkeaOneTap.KEY_ON, SalonOneTap.KEY_ON, NsOneTap.KEY_ON,
        BabyOneTap.KEY_ON, LowBattOneTap.KEY_ON
    )

    fun toggle(context: Context) {
        val ed = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        CHIP_KEYS.forEach { ed.putBoolean(it, false) }
        ed.putString("last_scene_id", "default")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.OFF)
        OneTapProfiles.apply(context, "off")
        DspControlService.start(context)
        Toast.makeText(context, "Alle context-chips uit", Toast.LENGTH_SHORT).show()
    }
}
