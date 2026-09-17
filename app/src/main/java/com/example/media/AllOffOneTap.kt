package com.example.media

import android.content.Context
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SoftwareAnc

/** Zet alle context-chips uit en ANC op OFF. */
object AllOffOneTap {
    private val CHIP_KEYS = listOf(
        "call_chip_on", "podcast_chip_on", "flight_chip_on", "game_chip_on",
        "movie_chip_on", "rain_chip_on", "kitchen_chip_on", "walk_chip_on",
        "focus_chip_on", "gym_chip_on", "sleep_chip_on", "commute_chip_on",
        "office_chip_on", "library_chip_on", "cafe_chip_on", "meeting_chip_on",
        "bike_chip_on", "shop_chip_on", "kids_chip_on", "train_chip_on",
        "car_chip_on", "metro_chip_on", "concert_chip_on", "restaurant_chip_on",
        "college_chip_on", "museum_chip_on", "build_chip_on",
        "beach_chip_on", "zoom_chip_on", "yoga_chip_on",
        "bus_chip_on", "market_chip_on", "wfh_chip_on",
        "garden_chip_on", "pool_chip_on", "sauna_chip_on",
        "run_chip_on", "clinic_chip_on", "traffic_chip_on",
        "ikea_chip_on", "salon_chip_on", "ns_chip_on",
        "baby_chip_on", "lowbatt_chip_on",
        "dogwalk_chip_on", "pharmacy_chip_on", "terrace_chip_on",
        "rainbike_chip_on", "ferry_chip_on", "silentwork_chip_on",
        "stadium_chip_on",
        "tram_chip_on", "bakery_chip_on", "parking_chip_on",
        "exam_chip_on", "laundry_chip_on", "privacyov_chip_on",
        "nachtbus_chip_on", "burenfeest_chip_on", "collegeecho_chip_on", "lekcheck_chip_on", "storm_chip_on", "kermis_chip_on", "partnerslaap_chip_on", "langecall_chip_on",
        "thuisbios_chip_on", "openkantoor_chip_on", "wachtkamer_chip_on", "afterwork_chip_on", "windfiets_chip_on", "nachtwerk_chip_on", "speeltuin_chip_on", "pomodoro_chip_on",
        "microbreak_chip_on", "longhaul_chip_on", "voiceisolate_chip_on", "duskride_chip_on", "latefps_chip_on", "softcall_chip_on"
    )

    fun clearAll(context: Context) {
        val e = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE).edit()
        CHIP_KEYS.forEach { e.putBoolean(it, false) }
        e.apply()
        SoftwareAnc.applyWithHardware(context, AncMode.OFF)
        OneTapProfiles.apply(context, "off")
        Toast.makeText(context, "Alle scenes uit", Toast.LENGTH_SHORT).show()
    }
}
