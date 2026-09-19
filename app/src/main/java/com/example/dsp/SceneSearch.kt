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
