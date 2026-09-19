package com.example.dsp

import android.content.Context
import com.example.data.SceneUsage

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
        val aliases = ALIASES[n].orEmpty()
        return SceneLookup.ALL.filter { s ->
            s.id.contains(n) ||
                s.id in aliases ||
                s.name.lowercase().contains(n) ||
                s.description.lowercase().contains(n) ||
                s.presetName.lowercase().contains(n) ||
                tagsFor(s.id).any { it.lowercase().contains(n) } ||
                (n == "fav" && s.id in favorites)
        }
    }

    private val ALIASES = mapOf(
        "efteling" to setOf("themepark"),
        "walibi" to setOf("themepark"),
        "zwembad" to setOf("poolreverb", "pool"),
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
        "ikea" to setOf("ikeazondag"),
        "tandarts" to setOf("tandartswacht"),
        "terras" to setOf("terraswind"),
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
        "gemeente" to setOf("gemeenteloket"),
        "loket" to setOf("gemeenteloket"),
        "postnl" to setOf("postnlpunt"),
        "pakket" to setOf("postnlpunt"),
        "etos" to setOf("etosrij"),
        "decathlon" to setOf("decathlonhal"),
        "tank" to setOf("tankstation"),
        "pomp" to setOf("tankstation"),
        "kapper" to setOf("kapperszaak"),
        "kapsalon" to setOf("kapperszaak"),
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
        "garage" to setOf("parkeergarage"),
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
        "slager" to setOf("slagerij"),
        "slagerij" to setOf("slagerij"),
        "foyer" to setOf("concertfoyer"),
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
        "zwemmen" to setOf("zwemles", "pool", "poolreverb", "zwembad"),
        "nachtbus" to setOf("nightbusplus", "nightbus"),
        "tentamen" to setOf("examhall", "exam"),
        "gehoor" to setOf("hearrest", "earfatigue"),
        "concert" to setOf("concertpit", "concert"),
        "kantoor" to setOf("openplanplus", "office", "openoffice"),
        "referentie" to setOf("refcheck", "reference"),
        "bellen" to setOf("callclarity", "call"),
        "wind" to setOf("windfietsplus", "wind"),
        "bieb" to setOf("libraryplus", "library"),
        "sportschool" to setOf("gympeak", "gym", "sport"),
        "regen" to setOf("rainwalkplus", "rain"),
        "keuken" to setOf("kitchensteam", "kitchen", "cook"),
        "nachtwerk" to setOf("latefocus", "latework"),
        "perron" to setOf("platformrush", "train", "transferhub"),
        "cafe" to setOf("cafechat", "cafe"),
        "slaap" to setOf("sleepwind", "sleep", "night")
    )

    fun queryRanked(context: Context, q: String, favorites: Set<String> = emptySet()): List<ListeningScene> {
        val hits = query(q, favorites)
        return hits.sortedByDescending { s ->
            val fav = if (s.id in favorites) 1000 else 0
            fav + SceneUsage.count(context, s.id)
        }
    }
}
