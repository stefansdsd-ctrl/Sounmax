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
            containsAny(p, "blinkist", "storytel", "scribd", "audible") -> "audiobook"
            containsAny(p, "primevideo", "netflix", "disney", "videolan", "jellyfin") -> "tv"
            containsAny(p, "tidal", "deezer", "soundcloud", "pandora") -> null
            else -> null
        }
    }

    private fun containsAny(hay: String, vararg keys: String): Boolean =
        keys.any { hay.contains(it) }
}
