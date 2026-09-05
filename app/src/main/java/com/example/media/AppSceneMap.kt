package com.example.media

/** Extra package → scene, bovenop ListeningScenes.fromNowPlaying. */
object AppSceneMap {
    fun sceneId(packageName: String?): String? {
        val p = packageName?.lowercase()?.takeIf { it.isNotBlank() } ?: return null
        return when {
            containsAny(p, "strava", "runtastic", "nike.plus", "nike.running",
                "adidas.running", "polar.beat", "runkeeper", "mapmyrun",
                "secuso.privacyFriendlyPedometer", "pedometer") -> "cardio"
            containsAny(p, "peloton", "zwift", "freeletics", "adidas.training",
                "homeworkout", "sevenminuteworkout", "thenx", "hevy", "strong.") -> "gym"
            containsAny(p, "duolingo", "babbel", "busuu", "memrise", "ankiweb", "anki") -> "language"
            containsAny(p, "coursera", "udemy", "khanacademy", "edx.mobile",
                "skillshare", "brilliant.org", "classroom") -> "lecture"
            containsAny(p, "waze", "google.android.apps.maps", "here.app",
                "sygic", "tomtom", "osmand", "maps.me", "komoot") -> "nav"
            containsAny(p, "yoga", "downdog", "downward", "asana.rebel", "dailyyoga") -> "yoga"
            containsAny(p, "alltrails", "geocaching", "outdooractive") -> "hike"
            containsAny(p, "chess", "lichess") -> "focus"
            containsAny(p, "bandcamp") -> "vinyl"
            containsAny(p, "castbox", "podcastaddict", "stitcher", "pocketcasts",
                "overcast", "castro", "antenna") -> "podcast"
            containsAny(p, "blinkist", "storytel", "scribd", "audible", "libro.fm",
                "libby", "overdrive") -> "audiobook"
            containsAny(p, "youtube.music", "youtubemusic") -> null
            containsAny(p, "primevideo", "netflix", "disney", "videolan", "jellyfin",
                "plex", "hbo", "nlziet", "videoland", "android.youtube") -> "tv"
            containsAny(p, "teams", "zoom", "meet", "webex", "slack") -> "meeting"
            containsAny(p, "discord", "ts3", "teamspeak") -> "voicechat"
            containsAny(p, "whatsapp", "telegram", "signal", "viber", "imessage") -> "call"
            containsAny(p, "nsandroid", "9292", "ovinfo", "ov-app", "ovpay",
                "ferry", "westerschelde") -> "commute"
            containsAny(p, "eurostar", "thalys", "dbnavigator", "db.navigator",
                "sncf", "trainline", "obb.tickets") -> "nsint"
            containsAny(p, "f1tv", "formula1", "fiaformula") -> "f1"
            containsAny(p, "espn", "dazn", "viaplay.sport", "ziggosport",
                "foxsports") -> "commentary"
            containsAny(p, "nos.nl", "nu.nl", "rtlnieuws", "ad.nl", "telegraaf") -> "news"
            containsAny(p, "kindle", "readera", "moon+reader", "librera",
                "kobo", "goodreads", "pocket") -> "ereader"
            containsAny(p, "tidal", "deezer", "soundcloud", "pandora") -> null
            containsAny(p, "notion", "obsidian", "vscode", "termux", "jetbrains",
                "openai.chatgpt", "anthropic.claude", "cursor", "figma") -> "coding"
            containsAny(p, "uber", "bolt", "free.now", "lyft") -> "car"
            containsAny(p, "klm", "booking", "expedia", "transavia", "ryanair") -> "plane"
            containsAny(p, "twitter", "x.android", "reddit") -> "news"
            containsAny(p, "calendar", "outlook", "gmail", "docs.editors",
                "android.gm", "sheets", "slides") -> "office"
            containsAny(p, "linkedin") -> "office"
            containsAny(p, "headspace", "calm", "insighttimer") -> "meditate"
            containsAny(p, "spotify") -> null
            containsAny(p, "tiktok", "instagram", "snapchat", "youtube.shorts",
                "bereal", "threads", "facebook") -> "shortform"
            containsAny(p, "steam", "epicgames", "playstation", "xboxapp",
                "roblox", "minecraft", "fortnite", "leagueoflegends", "twitch") -> "game"
            containsAny(p, "pokemongo", "ingress", "pikmin") -> "walk"
            containsAny(p, "flitsmeister", "anwb", "wdw") -> "car"
            containsAny(p, "ah.nl", "albertheijn", "jumbo", "lidl", "plus.nl",
                "picnic", "dirk", "aldi", "bol.com", "amazon", "ikea",
                "coolblue", "marktplaats", "action") -> "shop"
            containsAny(p, "thuisbezorgd", "deliveroo", "ubereats", "justeat",
                "nyt.cooking", "kitchenstories", "jow", "recipe") -> "cook"
            containsAny(p, "capcut", "vn.video", "inshot", "alight", "canva") -> "shortform"
            containsAny(p, "sleepcycle", "sleep.tracker", "pillow") -> "sleep"
            containsAny(p, "google.android.apps.fitness", "fitbit", "garmin") -> "cardio"
            containsAny(p, "thuisarts", "apotheek", "farmacie") -> "hospital"
            containsAny(p, "sauna", "wellness") -> "sauna"
            containsAny(p, "treatwell", "boekafspraak", "salonized") -> "barber"
            containsAny(p, "wework", "spaces.nl", "regus") -> "cowork"
            containsAny(p, "audiogids", "izi.travel", "smartify") -> "museumtour"
            containsAny(p, "viagogo", "ticketmaster", "eventim") -> "stadium"
            containsAny(p, "tikkie", "bunq", "ing.mobile", "rabobank", "abnamro") -> null
            containsAny(p, "crunchyroll", "funimation", "hidive", "bilibili") -> "anime"
            containsAny(p, "smule", "starmaker", "singa", "karaoke") -> "karaoke"
            containsAny(p, "forest", "focusto", "brain.fm", "endel") -> "exam"
            containsAny(p, "owlet", "babyphone", "nanit", "babymonitor") -> "baby"
            containsAny(p, "camping", "natuurkampeer", "pitchup") -> "camping"
            containsAny(p, "plexamp", "poweramp", "neutron", "foobar") -> null
            containsAny(p, "whatsapp.w4b", "conversations") -> "call"
            containsAny(p, "google.android.apps.messaging") -> "call"
            containsAny(p, "telegram.x", "org.telegram") -> "call"
            containsAny(p, "church", "youversion", "bible") -> "church"
            containsAny(p, "doordash") -> "cook"
            containsAny(p, "myswimpro", "swim.com", "form.swim", "zwem") -> "pool"
            containsAny(p, "gcam", "google.android.GoogleCamera", "lightroom",
                "snapseed", "vsco", "photoshop.express", "halide") -> "photoshoot"
            containsAny(p, "youtubekids", "youtube.kids") -> "kids"
            containsAny(p, "kick.live", "kick.com", "trovo", "afreeca") -> "livestream"
            containsAny(p, "genesys", "five9", "zendesk", "freshdesk",
                "nice.incontact") -> "callcenter"
            containsAny(p, "perplexity", "google.android.apps.bard", "gemini") -> "coding"
            containsAny(p, "schoolplanner", "somtoday", "magister") -> "school"
            containsAny(p, "npo.", "nporadio", "npoplus", "radio.nl", "tunelin", "tunein",
                "shazam", "soundhound") -> "radio_nl"
            containsAny(p, "viaplay", "ziggo", "kijk.nl", "nlziet", "appletv",
                "paramount", "bbc.iplayer", "nl.avrotros") -> "tv"
            containsAny(p, "bandlab", "flstudio", "cubasis", "garageband",
                "n-track", "audiolab") -> "studio"
            containsAny(p, "genshin", "honkai", "clashofclans", "clashroyale",
                "brawlstars", "pokemonunite", "valorant") -> "esports"
            containsAny(p, "tachyon", "duo.android", "facetime", "whereby",
                "jitsi") -> "videocall"
            containsAny(p, "buienradar", "buienalarm", "weather.yahoo") -> "rain"
            containsAny(p, "wikipedia", "wikimedia") -> "lecture"
            containsAny(p, "tesla", "android.car") -> "car"
            containsAny(p, "slopes", "piste", "onx.ski", "wintersport") -> "ski"
            containsAny(p, "parkrun", "komoot") -> "park"
            containsAny(p, "myfitnesspal", "yazio", "lifesum") -> "gym"
            containsAny(p, "kinemaster", "premiere.rush", "davinci") -> "shortform"
            containsAny(p, "apple.android.music", "amazon.mp3") -> null
            else -> null
        }
    }

    private fun containsAny(hay: String, vararg keys: String): Boolean =
        keys.any { hay.contains(it) }
}
