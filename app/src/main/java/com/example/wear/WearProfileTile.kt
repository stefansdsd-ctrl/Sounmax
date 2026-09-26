package com.example.wear

import android.content.Context
import com.example.data.HomeToolProfiles

/** Wear-tegel volgt het actieve home-profiel (Sport / Werk / Slaap). */
object WearProfileTile {
    data class Tile(
        val profileId: String,
        val name: String,
        val shortcuts: List<String>,
        val defaultEq: String,
        val defaultOneTap: String
    )

    fun current(context: Context): Tile = forProfile(HomeToolProfiles.activeId(context))

    fun forProfile(id: String): Tile = when (id) {
        "sport" -> Tile(
            "sport", "Sport",
            listOf("outdoor", "talkthrough", "reconnect", "haptic"),
            WearPaths.CMD_EQ_BIKE,
            WearPaths.CMD_ONE_TAP_GYM
        )
        "slaap" -> Tile(
            "slaap", "Slaap",
            listOf("sleeptimer", "night", "quiet", "volcap"),
            WearPaths.CMD_EQ_SLEEP,
            WearPaths.CMD_ONE_TAP_SLEEP
        )
        else -> Tile(
            "werk", "Werk",
            listOf("meeting", "talkboost", "focus", "duck"),
            WearPaths.CMD_EQ_WORK,
            WearPaths.CMD_ONE_TAP_OFFICE
        )
    }

    fun cycle(context: Context): Tile {
        val ids = HomeToolProfiles.ALL.map { it.id }
        val cur = HomeToolProfiles.activeId(context)
        val next = ids[(ids.indexOf(cur) + 1).coerceAtLeast(0) % ids.size]
        HomeToolProfiles.set(context, next)
        return forProfile(next)
    }
}
