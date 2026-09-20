package com.example.dsp

import android.content.Context
import com.example.data.SceneUsage
import kotlin.math.min

/** Zoek + tags over alle luister-scenes. */
object SceneSearch {
    fun tagsFor(id: String): List<String> =
        SceneGroups.LABELS
            .filter { it.second.contains(id) }
            .map { it.first }
            .filter { it != "Alles" && it != "Favorieten" }

    fun query(q: String, favorites: Set<String> = emptySet()): List<ListeningScene> {
        val n = q.trim().lowercase()
        if (n.isEmpty()) return SceneLookup.ALL
        val aliases = mutableSetOf<String>()
        ALIASES[n]?.let { aliases += it }
        ALIASES.forEach { (key, ids) ->
            if (key.startsWith(n) || n.startsWith(key)) aliases += ids
        }
        val exact = SceneLookup.ALL.filter { s ->
            s.id.contains(n) ||
                s.id in aliases ||
                s.name.lowercase().contains(n) ||
                s.description.lowercase().contains(n) ||
                s.presetName.lowercase().contains(n) ||
                tagsFor(s.id).any { it.lowercase().contains(n) } ||
                (n == "fav" && s.id in favorites)
        }
        if (exact.isNotEmpty() || n.length < 4) return exact
        return SceneLookup.ALL.filter { s ->
            editDistance(n, s.id) <= 2 || editDistance(n, s.name.lowercase()) <= 2
        }
    }

    private fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        if (kotlin.math.abs(a.length - b.length) > 2) return 99
        val m = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) m[i][0] = i
        for (j in 0..b.length) m[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                m[i][j] = min(min(m[i - 1][j] + 1, m[i][j - 1] + 1), m[i - 1][j - 1] + cost)
            }
        }
        return m[a.length][b.length]
    }

    private val ALIASES = mapOf(
        "efteling" to setOf("themepark"),
        "walibi" to setOf("themepark"),
        "zwembad" to setOf("poolreverb", "pool", "zwembadtribune", "zwemles"),
        "kermis" to setOf("fairground"),
        "intratuin" to setOf("gardencenter"),
        "gamma" to setOf("diyhall"),
        "praxis" to setOf("diyhall"),
        "karwei" to setOf("diyhall"),
        "kringloop" to setOf("thriftshop"),
        "overstap" to setOf("transferhub", "ovoverstap", "nsoverstap"),
        "huisarts" to setOf("huisartswacht"),
        "klus" to setOf("klusweekend"),
        "balkon" to setOf("regenbalcon"),
        "fietstunnel" to setOf("fietstunnel"),
        "jumbo" to setOf("jumbospits"),
        "ikea" to setOf("ikeazondag", "ikearestaurant"),
        "tandarts" to setOf("tandartswacht", "tandartsstoel"),
        "terras" to setOf("terraswind", "dakterras"),
        "lift" to setOf("liftecho"),
        "kids" to setOf("thuiskidsplus"),
        "lidl" to setOf("lidlspits"),
        "hema" to setOf("hemarij"),
        "mediamarkt" to setOf("mediamarktgang"),
        "fietskelder" to setOf("fietskelder"),
        "bushalte" to setOf("bushaltekou"),
        "coolblue" to setOf("coolbluepickup"),
        "action" to setOf("actionhal"),
        "kruidvat" to setOf("kruidvatrij"),
        "ahtogo" to setOf("ahtogo"),
        "ah to go" to setOf("ahtogo"),
        "pr" to setOf("prparkeren"),
        "p+r" to setOf("prparkeren"),
        "wasstraat" to setOf("wasstraat"),
        "gemeente" to setOf("gemeenteloket", "gemeentebalie"),
        "loket" to setOf("gemeenteloket"),
        "postnl" to setOf("postnlpunt", "pakketpunt"),
        "pakket" to setOf("postnlpunt", "pakketpunt"),
        "dhl" to setOf("pakketpunt"),
        "etos" to setOf("etosrij"),
        "decathlon" to setOf("decathlonhal"),
        "tank" to setOf("tankstation"),
        "pomp" to setOf("tankstation"),
        "kapper" to setOf("kapperszaak", "kappersstoel"),
        "kapsalon" to setOf("kapperszaak", "kappersstoel"),
        "kapperstoel" to setOf("kappersstoel"),
        "flixbus" to setOf("flixbus"),
        "flix" to setOf("flixbus"),
        "ovpoort" to setOf("ovchippoort"),
        "poortjes" to setOf("ovchippoort"),
        "ovchip" to setOf("ovchippoort"),
        "snackbar" to setOf("snackbar"),
        "cafetaria" to setOf("snackbar"),
        "friet" to setOf("snackbar"),
        "woonboulevard" to setOf("woonboulevard"),
        "ikea woon" to setOf("woonboulevard"),
        "mcdrive" to setOf("mcdrive"),
        "mcdonalds" to setOf("mcdrive"),
        "drive" to setOf("mcdrive"),
        "icd" to setOf("icdirect"),
        "intercity" to setOf("icdirect"),
        "ic direct" to setOf("icdirect"),
        "picnic" to setOf("picnicbezorg"),
        "bezorg" to setOf("picnicbezorg", "thuisbezorgd"),
        "zelfscan" to setOf("zelfscan"),
        "scan" to setOf("zelfscan"),
        "stationshal" to setOf("stationshal"),
        "cs" to setOf("stationshal"),
        "hornbach" to setOf("hornbach"),
        "bouwmarkt" to setOf("hornbach", "diyhall"),
        "parkeergarage" to setOf("parkeergarage"),
        "garage" to setOf("parkeergarage", "garagebox", "apkkeuring"),
        "apk" to setOf("apkkeuring"),
        "thuisbezorgd" to setOf("thuisbezorgd"),
        "justeat" to setOf("thuisbezorgd"),
        "takeaway" to setOf("thuisbezorgd"),
        "ijssalon" to setOf("ijssalon"),
        "ijs" to setOf("ijssalon"),
        "gelato" to setOf("ijssalon"),
        "apotheek" to setOf("apotheek"),
        "pharmacy" to setOf("apotheek"),
        "kinderopvang" to setOf("kinderopvang"),
        "opvang" to setOf("kinderopvang"),
        "creche" to setOf("kinderopvang"),
        "locker" to setOf("fitnesslocker"),
        "kleedkamer" to setOf("fitnesslocker"),
        "storm" to setOf("stormfiets"),
        "stormfiets" to setOf("stormfiets"),
        "slager" to setOf("slagerij", "slagerijplus"),
        "slagerij" to setOf("slagerij", "slagerijplus"),
        "foyer" to setOf("concertfoyer", "bioscoopfoyer"),
        "concertfoyer" to setOf("concertfoyer"),
        "bakkerij" to setOf("bakkerij"),
        "bakker" to setOf("bakkerij"),
        "brood" to setOf("bakkerij"),
        "dierenarts" to setOf("dierenarts"),
        "vet" to setOf("dierenarts"),
        "dierenkliniek" to setOf("dierenarts"),
        "padel" to setOf("padelbaan"),
        "padelbaan" to setOf("padelbaan"),
        "klimhal" to setOf("klimhal"),
        "boulderen" to setOf("klimhal"),
        "klimmen" to setOf("klimhal"),
        "bowling" to setOf("bowlingbaan"),
        "bowlingbaan" to setOf("bowlingbaan"),
        "zwemles" to setOf("zwemles"),
        "zwemmen" to setOf("zwemles", "pool", "poolreverb", "zwembadtribune"),
        "tribune" to setOf("zwembadtribune"),
        "nachtbus" to setOf("nightbusplus", "nightbus"),
        "tentamen" to setOf("examhall", "exam"),
        "gehoor" to setOf("hearrest", "earfatigue"),
        "concert" to setOf("concertpit", "concert"),
        "kantoor" to setOf("openplanplus", "office", "openoffice", "kantoortuin", "kantoorflex"),
        "referentie" to setOf("refcheck", "reference"),
        "bellen" to setOf("callclarity", "call", "callbooth"),
        "wind" to setOf("windfietsplus", "wind", "snelfietspad"),
        "bieb" to setOf("libraryplus", "library", "studiezaal", "bibliobus", "biebfoyer"),
        "studiezaal" to setOf("studiezaal"),
        "sportschool" to setOf("gympeak", "gym", "sport"),
        "regen" to setOf("rainwalkplus", "rain"),
        "keuken" to setOf("kitchensteam", "kitchen", "cook", "keukentafelavond"),
        "nachtwerk" to setOf("latefocus", "latework"),
        "perron" to setOf("platformrush", "train", "transferhub"),
        "cafe" to setOf("cafechat", "cafe"),
        "slaap" to setOf("sleepwind", "sleep", "night"),
        "rijles" to setOf("rijles"),
        "cbr" to setOf("cbrtheorie", "rijles"),
        "theorie" to setOf("cbrtheorie"),
        "ziekenhuis" to setOf("ziekenhuiswacht", "ziekenhuis"),
        "wachtkamer" to setOf("ziekenhuiswacht", "tandartswacht", "huisartswacht", "wachtkamerplus"),
        "lab" to setOf("practicumlab"),
        "practicum" to setOf("practicumlab"),
        "sportdag" to setOf("sportdag"),
        "douche" to setOf("douchepodcast", "campingdouche"),
        "badkamer" to setOf("douchepodcast"),
        "fysio" to setOf("fysio"),
        "fysiotherapie" to setOf("fysio"),
        "revalidatie" to setOf("fysio"),
        "bioscoop" to setOf("bioscoopzaal", "bioscoop", "bioscoopfoyer"),
        "cinema" to setOf("bioscoopzaal", "bioscoop"),
        "filmhuis" to setOf("bioscoopzaal", "bioscoop"),
        "film" to setOf("bioscoopzaal"),
        "sauna" to setOf("sauna"),
        "wellness" to setOf("sauna"),
        "schaatsen" to setOf("schaatsbaan"),
        "schaatsbaan" to setOf("schaatsbaan"),
        "ijsbaan" to setOf("schaatsbaan"),
        "werkcollege" to setOf("werkcollege"),
        "werkgroep" to setOf("werkcollege"),
        "tutorial" to setOf("werkcollege"),
        "speeltuin" to setOf("speeltuin"),
        "playground" to setOf("speeltuin"),
        "hardlopen" to setOf("hardlopen"),
        "rennen" to setOf("hardlopen"),
        "joggen" to setOf("hardlopen"),
        "running" to setOf("hardlopen"),
        "teams" to setOf("teamsvergadering"),
        "zoom" to setOf("teamsvergadering"),
        "meet" to setOf("teamsvergadering"),
        "vergadering" to setOf("teamsvergadering"),
        "meeting" to setOf("teamsvergadering"),
        "dierentuin" to setOf("dierentuin"),
        "zoo" to setOf("dierentuin"),
        "e-step" to setOf("estepwind"),
        "estep" to setOf("estepwind"),
        "scooter" to setOf("estepwind"),
        "nachtmarkt" to setOf("nachtmarkt"),
        "wasruimte" to setOf("wasruimte"),
        "droger" to setOf("wasruimte"),
        "wasmachine" to setOf("wasruimte"),
        "bbq" to setOf("barbecue"),
        "tuinfeest" to setOf("barbecue"),
        "kantoortuin" to setOf("kantoortuin"),
        "uitvaart" to setOf("uitvaart"),
        "crematorium" to setOf("uitvaart"),
        "rechtbank" to setOf("rechtbank"),
        "zitting" to setOf("rechtbank"),
        "notaris" to setOf("notaris"),
        "akte" to setOf("notaris"),
        "fietsenmaker" to setOf("fietsenmaker"),
        "waterbus" to setOf("waterbus"),
        "pontje" to setOf("waterbus"),
        "pont" to setOf("waterbus"),
        "consultatie" to setOf("consultatiebureau"),
        "ggd" to setOf("consultatiebureau"),
        "cjg" to setOf("consultatiebureau"),
        "skatepark" to setOf("skatepark"),
        "skate" to setOf("skatepark"),
        "milieustraat" to setOf("milieustraat"),
        "recycling" to setOf("milieustraat"),
        "afval" to setOf("milieustraat"),
        "kantine" to setOf("schoolkantine", "voetbalkantine", "kantoorkantine"),
        "schoolkantine" to setOf("schoolkantine"),
        "buurthuis" to setOf("buurthuis"),
        "wijkcentrum" to setOf("buurthuis"),
        "huiswerk" to setOf("huiswerktafel"),
        "campus" to setOf("collegecampus"),
        "markt" to setOf("marktplein"),
        "ziekenhuisgang" to setOf("ziekenhuisgang"),
        "biebfoyer" to setOf("biebfoyer"),
        "peuter" to setOf("peuterspeelzaal"),
        "peuterspeelzaal" to setOf("peuterspeelzaal"),
        "snelfietspad" to setOf("snelfietspad"),
        "snelwegfiets" to setOf("snelfietspad"),
        "bibliobus" to setOf("bibliobus"),
        "biebbus" to setOf("bibliobus"),
        "flexkantoor" to setOf("kantoorflex"),
        "hotdesk" to setOf("kantoorflex"),
        "kantoorflex" to setOf("kantoorflex"),
        "keukentafel" to setOf("keukentafelavond"),
        "wasserette" to setOf("wasserette"),
        "huisgenoot" to setOf("studentenhuis"),
        "studentenhuis" to setOf("studentenhuis"),
        "lunchwandeling" to setOf("lunchwandeling"),
        "stiltekamer" to setOf("stilteruimte"),
        "stilteruimte" to setOf("stilteruimte"),
        "trappenhuis" to setOf("trapportaal"),
        "trapportaal" to setOf("trapportaal"),
        "nachtbushalte" to setOf("avondbushalte"),
        "avondbushalte" to setOf("avondbushalte"),
        "foodplaza" to setOf("foodcourt"),
        "foodcourt" to setOf("foodcourt"),
        "yoga" to setOf("yogales"),
        "yogales" to setOf("yogales"),
        "fablab" to setOf("makerspace"),
        "makerspace" to setOf("makerspace"),
        "polder" to setOf("polderweg"),
        "polderweg" to setOf("polderweg"),
        "klusbox" to setOf("garagebox"),
        "garagebox" to setOf("garagebox"),
        "buurtwinkel" to setOf("buurtsuper"),
        "buurtsuper" to setOf("buurtsuper"),
        "strandtent" to setOf("strandpaviljoen"),
        "paviljoen" to setOf("strandpaviljoen"),
        "strandpaviljoen" to setOf("strandpaviljoen"),
        "bouwkeet" to setOf("bouwkeet"),
        "keet" to setOf("bouwkeet"),
        "overweg" to setOf("spoorwegovergang"),
        "spoorwegovergang" to setOf("spoorwegovergang"),
        "studentenkamer" to setOf("studentenkamer"),
        "studentenkot" to setOf("studentenkamer"),
        "bioscoopfoyer" to setOf("bioscoopfoyer"),
        "filmfoyer" to setOf("bioscoopfoyer"),
        "gemeentebalie" to setOf("gemeentebalie"),
        "balie" to setOf("gemeentebalie", "gemeenteloket"),
        "tunnel" to setOf("fietstunnel"),
        "dak" to setOf("dakterras"),
        "dakterras" to setOf("dakterras"),
        "nachtwinkel" to setOf("nachtwinkel"),
        "avondwinkel" to setOf("nachtwinkel"),
        "treinwerk" to setOf("treinwerk"),
        "laptoptrein" to setOf("treinwerk"),
        "praktijklokaal" to setOf("praktijklokaal"),
        "lokaal" to setOf("praktijklokaal"),
        "kantoorkantine" to setOf("kantoorkantine"),
        "theater" to setOf("theaterzaal"),
        "schouwburg" to setOf("theaterzaal"),
        "kerk" to setOf("kerkzaal"),
        "picknick" to setOf("picknickpark"),
        "belcel" to setOf("callbooth"),
        "callbooth" to setOf("callbooth"),
        "nachttrein" to setOf("nachttreinwerk", "nachttrein")
    )

    fun queryRanked(context: Context, q: String, favorites: Set<String> = emptySet()): List<ListeningScene> {
        val n = q.trim().lowercase()
        val hits = query(q, favorites)
        return hits.sortedByDescending { s ->
            val name = s.name.lowercase()
            val exact = if (s.id == n || name == n) 200 else 0
            val prefix = if (s.id.startsWith(n) || name.startsWith(n)) 80 else 0
            val fav = if (s.id in favorites) 1000 else 0
            fav + exact + prefix + SceneUsage.count(context, s.id) + SceneUsage.recencyScore(context, s.id)
        }
    }
}
