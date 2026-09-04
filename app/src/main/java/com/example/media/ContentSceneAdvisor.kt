package com.example.media

import android.content.SharedPreferences
import com.example.data.NowPlayingApp
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneLookup

/** Media wint van tijd-scene, niet van weer/accu/vlucht. */
object ContentSceneAdvisor {
    private val KEEP = setOf(
        "rain", "wind", "commute_rain", "bike_rain", "plane", "saver", "rest", "airport"
    )

    private var prefs: SharedPreferences? = null

    fun adjust(scene: ListeningScene): ListeningScene {
        val pkg = NowPlayingApp.packageName?.takeIf { it.isNotBlank() } ?: return scene
        if (scene.id in KEEP) return scene
        prefs?.let { p ->
            AppSceneMemory.recall(p, pkg)?.let { id ->
                SceneLookup.byId(id)?.let { return it }
            }
        }
        AppSceneMap.sceneId(pkg)?.let { id ->
            SceneLookup.byId(id)?.let { return it }
        }
        ListeningScenes.fromNowPlaying(pkg, NowPlayingApp.genre.orEmpty(), NowPlayingApp.blob())
            ?.let { return it }
        return scene
    }

    fun remember(prefs: SharedPreferences) {
        this.prefs = prefs
        prefs.edit()
            .putString("np_pkg", NowPlayingApp.packageName)
            .putString("np_title", NowPlayingApp.title)
            .putString("np_artist", NowPlayingApp.artist)
            .putString("np_genre", NowPlayingApp.genre)
            .apply()
        AppSceneMemory.remember(prefs, NowPlayingApp.packageName, prefs.getString("last_scene_id", null))
    }

    fun restore(prefs: SharedPreferences) {
        this.prefs = prefs
        if (!NowPlayingApp.packageName.isNullOrBlank()) return
        NowPlayingApp.packageName = prefs.getString("np_pkg", null)
        NowPlayingApp.title = prefs.getString("np_title", null)
        NowPlayingApp.artist = prefs.getString("np_artist", null)
        NowPlayingApp.genre = prefs.getString("np_genre", null)
    }
}
