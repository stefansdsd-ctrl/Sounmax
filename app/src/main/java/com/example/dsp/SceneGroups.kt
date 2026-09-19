package com.example.dsp

/** UI-groepen inclusief extra scenes (Shorts/Tuin/Wachten e.d.). */
object SceneGroups {
    val LABELS: List<Pair<String, Set<String>>> = listOf(
        "Alles" to emptySet(),
        "Favorieten" to emptySet(),
        "Onderweg" to setOf(
            "commute", "train", "bus", "tram", "metro", "plane", "car", "bike", "walk",
            "station", "airport", "commute_rain", "nav", "rainwalk", "nightdrive",
            "wind", "bike_rain", "ferry", "waiting", "quietcar", "nsint", "raincar", "nightbus",
            "gvb", "douane", "ovchip", "swapfiets", "treinwerk", "nsdruk", "rain", "spits",
            "nachttrein", "intercity", "regenfiets", "wasstraat", "podcastwalk", "avondwandeling", "treinstilte",
            "onweer", "sneeuw", "schoolochtend", "avondfiets", "file", "ovoverstap", "bouw",
            "rainwalk", "longhaul", "duskride", "platformwait",
            "carwashpro", "boattrip", "rainmetro", "pickupkids", "lunchwalk", "packedtram", "quietcoach",
            "garageecho", "morningbike", "nighttram", "trackwork", "nightbusplus", "transferhub", "platformrush", "rainwalkplus", "nsoverstap", "fietstunnel", "jumbospits"
        ),
        "Werk" to setOf(
            "focus", "deepwork", "office", "latework", "wfh", "meeting", "school",
            "study", "library", "lecture", "language", "coding", "interview", "workshop", "cowork", "exam", "callcenter", "videocall", "openoffice",
            "hackathon", "collegezaal", "treinwerk", "praat", "bibliotheekstil", "kantooropen", "schoolochtend",
            "morningbrief", "voiceisolate", "softcall", "inboxzero", "neighborhood",
            "homeexam", "cafefocus", "longcall", "hybridmeet", "rainoffice", "libsilence", "libraryplus", "latefocus"
        ),
        "Sport" to setOf("sport", "gym", "hiit", "hike", "beach", "cardio", "yoga", "festival", "stadium", "pool", "ski", "homeworkout", "f1", "basicfit", "sportschool", "shortgym", "boattrip", "morningbike", "themepark", "poolreverb", "fairground", "gympeak", "rainwalkplus"),
        "Media" to setOf(
            "film", "cinema", "podcast", "audiobook", "news", "vinyl", "jazz", "classic",
            "lofistudy", "concert", "anime", "radio", "tv", "djset", "shortform", "piano", "acoustic",
            "museumtour", "livesport", "karaoke", "livestream", "ereader", "movienight", "radio_nl",
            "f1", "podcast_nl", "commentary", "bedpodcast", "museum_night", "voiceboost", "concertzaal", "voetbal_thuis", "museum"
        ),
        "Genre" to setOf(
            "classic", "dnb", "nederhop", "lofistudy", "synthwave", "hyperpop",
            "hardstyle", "gabber", "phonk", "kpop", "afrobeat", "nederpop",
            "reggae", "latin", "anime", "country", "gospel", "boombap", "metalcore",
            "drill", "trance", "ukg", "jazz", "vinyl", "house", "techno", "dubstep", "liquid", "ambient"
        ),
        "Game" to setOf("game", "fps", "voicechat", "esports"),
        "Nacht" to setOf(
            "night", "sleep", "rest", "meditate", "asmr", "latework", "nature",
            "ambient", "saver", "liquid", "nightshift", "yoga", "nightdrive", "flightsleep", "sauna",
            "camping", "baby", "movienight", "quietcar", "museum_night", "memorial", "thuisavond", "nachttrein", "zondagochtend", "kerk", "avondwandeling", "treinstilte", "winddown", "avondfiets", "bedpodcast",
            "sundayreset", "batterysave", "microbreak", "stillnight", "sundayafter", "neighbors", "kidsbed", "latefocus", "sleepwind", "thuiskidsplus"
        ),
        "Dag" to setOf("morning", "cafe", "office", "wfh", "cook", "garden", "market", "waiting", "shop", "barber", "cowork", "dentist", "alert", "camping", "baby", "exam", "playground", "photoshoot", "pool", "park", "supermarket", "wedding", "pharmacy", "ikea", "construction", "horeca", "openoffice", "marktplein", "praat", "rain", "koffietent", "huisarts", "ziekenhuis", "thuiskids", "weekendmarkt", "terrasavond", "wachtrij", "avondwandeling", "hitte", "sneeuw", "schoolochtend", "kapper", "museum", "cookeve", "pharmacywait", "nightshop", "waitingroom", "diy", "garage", "lateah", "sundaymarket", "garageklus", "deliverydoor", "schoolyard", "hospwait", "themepark", "poolreverb", "fairground", "gardencenter", "diyhall", "thriftshop", "libraryplus", "kitchensteam", "platformrush", "cafechat", "jumbospits", "ikeazondag", "tandartswacht", "terraswind", "liftecho", "thuiskidsplus", "huisartswacht", "klusweekend", "regenbalcon"),
        "Tools" to setOf("basscheck", "stereotest", "reference", "speaker", "studio", "oneear", "mixcheck", "recap", "abcompare")
    )

    fun ids(group: String): Set<String>? =
        LABELS.firstOrNull { it.first == group }?.second
}
