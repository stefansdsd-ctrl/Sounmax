package com.example.data

/** Koppelt now-playing genre/titel aan een home-profiel of scene-hint. */
object GenreSceneHint {
    data class Hint(val profileId: String, val label: String)

    fun from(blob: String?): Hint? {
        val t = blob?.lowercase()?.trim().orEmpty()
        if (t.isBlank()) return null
        return when {
            any(t, "sleep", "slaap", "lofi", "ambient", "rain", "white noise") ->
                Hint("slaap", "Slaap — rustig / lofi")
            any(t, "workout", "gym", "sport", "running", "edm", "techno", "metal") ->
                Hint("sport", "Sport — beat / gym")
            any(t, "podcast", "audiobook", "speech", "news", "talk") ->
                Hint("werk", "Werk — spraak")
            any(t, "classical", "jazz", "acoustic", "piano") ->
                Hint("werk", "Werk — akoestisch")
            else -> null
        }
    }

    fun fromNowPlaying(): Hint? = from(NowPlayingApp.blob())

    fun applyIfEnabled(context: android.content.Context): Hint? {
        if (!AutoHomeProfile.enabled(context)) return null
        val hint = fromNowPlaying() ?: return null
        if (HomeToolProfiles.activeId(context) != hint.profileId) {
            HomeToolProfiles.set(context, hint.profileId)
        }
        return hint
    }

    private fun any(hay: String, vararg needles: String): Boolean =
        needles.any { hay.contains(it) }
}
