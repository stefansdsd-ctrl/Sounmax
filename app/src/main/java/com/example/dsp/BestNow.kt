package com.example.dsp

import android.content.Context
import com.example.data.FavoriteScenes
import com.example.data.SceneUsage
import com.example.media.HourSceneSuggest
import com.example.media.ListenDose
import java.util.Calendar

/** Combineert usage, uur, favorieten, weekend, accu en luisterdosis tot “beste nu”. */
object BestNow {
    fun ranked(context: Context, limit: Int = 5): List<ListeningScene> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val hourHint = HourSceneSuggest.suggest(context)?.id
        val favs = runCatching { FavoriteScenes(context).ids().toSet() }.getOrDefault(emptySet())
        val batt = runCatching { PhoneBattery.percent(context) }.getOrDefault(100)
        return SceneLookup.ALL
            .map { scene ->
                val uses = SceneUsage.count(context, scene.id)
                val last = context.getSharedPreferences("sounmax_scene_usage", Context.MODE_PRIVATE)
                    .getLong("last_${scene.id}", 0L)
                val recency = if (last == 0L) 0 else ((System.currentTimeMillis() - last) / 3_600_000L).toInt().let { h ->
                    when {
                        h < 2 -> 40
                        h < 24 -> 20
                        h < 72 -> 8
                        else -> 0
                    }
                }
                val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
                val score = uses * 3 +
                    recency +
                    (if (scene.id in favs) 25 else 0) +
                    (if (scene.id == hourHint) 30 else 0) +
                    hourBias(hour, scene.id) +
                    weekendBias(weekend, scene.id) +
                    batteryBias(batt, scene.id) +
                    doseBias(context, scene.id) +
                    noiseBias(scene)
                scene to score
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .distinctBy { it.id }
            .take(limit)
    }

    fun top(context: Context): ListeningScene? = ranked(context, 1).firstOrNull()

    fun label(context: Context): String? =
        top(context)?.let { "Beste nu: ${it.emoji} ${it.name}" }

    private fun hourBias(hour: Int, id: String): Int = when {
        hour in 6..8 && id in setOf("commute", "train", "metro", "windfietsplus", "platformrush", "rainbikeplus", "mondaystart", "rainplatform", "tramspits", "ebikewind", "slaaptrein", "bakfiets", "stiltecoupé", "nsoverstap", "fietstunnel", "snelfietspad", "bushaltekou", "fietskelder", "prparkeren", "ahtogo", "tankstation", "flixbus", "ovchippoort", "icdirect", "stationshal", "parkeergarage", "stormfiets", "bakkerij", "hardlopen", "estepwind", "waterbus", "fietsenstalling", "fietskoerier", "polderbus", "bakfietsrit", "regenoverkapping", "schoolfiets") -> 12
        hour in 7..9 && id in setOf("schoolplein", "rijles", "sportdag", "hardlopen", "schoolkantine", "bso", "collegeopname", "schoolfiets") -> 14
        hour in 9..17 && id in setOf("fysio", "werkcollege", "cbrtheorie", "practicumlab", "ziekenhuiswacht", "openplanplus", "focus", "office", "examhall", "libraryplus", "zoomclass", "hotdesk", "coworkcall", "ahspits", "regenkantoor", "collegezaal", "bouwstraat", "wasdroger", "huisartswacht", "biebavond", "jumbospits", "tandartswacht", "liftecho", "thuiskidsplus", "lidlspits", "hemarij", "mediamarktgang", "coolbluepickup", "actionhal", "kruidvatrij", "gemeenteloket", "postnlpunt", "etosrij", "decathlonhal", "kapperszaak", "woonboulevard", "picnicbezorg", "zelfscan", "hornbach", "stationshal", "ijssalon", "apotheek", "slagerij", "fitnesslocker", "bakkerij", "dierenarts", "klimhal", "teamsvergadering", "wasruimte", "tandartsstoel", "pakketpunt", "kantoortuin", "apkkeuring", "rechtbank", "notaris", "fietsenmaker", "consultatiebureau", "studiezaal", "buurthuis", "kappersstoel", "kantoorpantry", "schouwburg", "kantoorflex", "peuterspeelzaal", "slagerijplus", "bibliobus", "stilteruimte", "studentenhuis", "trapportaal", "collegeopname", "thuiswassen", "koffiecorner", "koelcel", "treinbistro") -> 10
        hour in 17..20 && id in setOf("speeltuin", "schaatsbaan", "gympeak", "cafechat", "kitchensteam", "traffichold", "tvavond", "vrijdagavond", "drukkoken", "keukenbellen", "avondmarkt", "terraswind", "jumbospits", "lidlspits", "bushaltekou", "actionhal", "kruidvatrij", "ahtogo", "prparkeren", "tankstation", "postnlpunt", "flixbus", "snackbar", "mcdrive", "picnicbezorg", "ovchippoort", "zelfscan", "thuisbezorgd", "parkeergarage", "ijssalon", "kinderopvang", "fitnesslocker", "slagerij", "padelbaan", "klimhal", "bowlingbaan", "zwemles", "hardlopen", "estepwind", "barbecue", "pakketpunt", "waterbus", "skatepark", "fietsenmaker", "schoolkantine", "zwembadtribune", "milieustraat", "buurthuis", "bso", "sportkleedkamer", "stadspark", "fietsenstalling", "keukentafelavond", "snelfietspad", "lunchwandeling", "avondbushalte", "fietskoerier", "thuiswassen", "glasbak", "zwembadgang") -> 10
        hour in 20..23 && id in setOf("bioscoop", "sauna", "avondwandel", "avondmarkt", "regenbalcon", "biebavond", "flixbus", "snackbar", "icdirect", "thuisbezorgd", "ijssalon", "concertfoyer", "nachtmarkt", "wasruimte", "zwembadtribune", "schouwburg", "avondbushalte", "wasserette", "studentenhuis", "nachtportier", "oorpauze", "nachtparkeer") -> 12
        hour in 22..23 || hour < 6 && id in setOf("hearrest", "earfatigue", "night", "sleep", "latefocus", "sleepwind", "latebus", "slaaptrein", "flixbus", "icdirect", "douchepodcast", "wasruimte", "nachtportier", "oorpauze", "nachtparkeer") -> 14
        else -> 0
    }

    private fun weekendBias(weekend: Boolean, id: String): Int {
        if (!weekend) return 0
        return if (id in setOf("themepark", "fairground", "cafechat", "rainwalkplus", "concertpit", "sundayreset", "vrijdagavond", "ahspits", "avondwandel", "avondmarkt", "klusweekend", "regenbalcon", "ikeazondag", "terraswind", "mediamarktgang", "hemarij", "coolbluepickup", "actionhal", "wasstraat", "prparkeren", "decathlonhal", "etosrij", "flixbus", "woonboulevard", "snackbar", "mcdrive", "icdirect", "hornbach", "ijssalon", "stationshal", "zelfscan", "concertfoyer", "slagerij", "stormfiets", "padelbaan", "klimhal", "bowlingbaan", "zwemles", "bakkerij", "sportdag", "rijles", "bioscoop", "sauna", "schaatsbaan", "speeltuin", "hardlopen", "dierentuin", "nachtmarkt", "barbecue", "pakketpunt", "apkkeuring", "waterbus", "skatepark", "fietsenmaker", "consultatiebureau", "milieustraat", "zwembadtribune", "buurthuis", "kappersstoel", "stadspark", "schouwburg", "sportkleedkamer", "bibliobus", "keukentafelavond", "snelfietspad", "wasserette", "lunchwandeling", "studentenhuis", "polderbus", "thuiswassen", "sluiswacht", "parkeerdek", "glasbak", "treinbistro", "zwembadgang", "nachtparkeer")) 8 else 0
    }

    private fun doseBias(context: Context, id: String): Int =
        if (ListenDose.shouldPause(context) && id in setOf("earfatigue", "hearrest", "sleep", "rest", "oorpauze")) 22 else 0

    private fun batteryBias(percent: Int, id: String): Int = when {
        percent <= 10 && id in setOf("saver", "batterysaveplus", "sleep", "rest", "oorpauze") -> 40
        percent <= 20 && id in setOf("saver", "batterysaveplus", "oorpauze") -> 28
        percent <= 20 && id in setOf("gympeak", "party", "festival", "concertpit", "padelbaan", "klimhal", "bowlingbaan", "hardlopen") -> -12
        else -> 0
    }

    /** Harde omgeving → ANC-scenes; stil → ambient/nacht. */
    private fun noiseBias(scene: ListeningScene): Int {
        val n = AmbientNoiseFloor.lastIntensity
        return when {
            n >= 0.72f && scene.ancMode in setOf(AncMode.STRONG, AncMode.ADAPTIVE, AncMode.WIND_GUARD) -> 16
            n >= 0.72f && scene.ancMode == AncMode.AMBIENT -> -6
            n <= 0.28f && scene.id in setOf("sleep", "night", "libraryplus", "stiltewerk", "hearrest", "cbrtheorie", "practicumlab", "sauna", "werkcollege", "teamsvergadering", "uitvaart", "kantoortuin", "rechtbank", "notaris", "studiezaal", "schouwburg", "oorpauze") -> 14
            n <= 0.28f && scene.ancMode == AncMode.STRONG -> -8
            else -> 0
        }
    }
}
