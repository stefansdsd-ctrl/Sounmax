package com.example.ui

data class YtTrack(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val duration: String,
    val recommendedPreset: String,
    val directUrl: String,
    val badge: String = "Hi-Res"
)

data class YtPlaylist(
    val id: String,
    val title: String,
    val description: String,
    val genre: String,
    val trackCount: Int,
    val recommendedPreset: String,
    val queryUrl: String,
    val colorStartHex: Long,
    val colorEndHex: Long
)

object CuratedYtMusicData {
    val PLAYLISTS = listOf(
        YtPlaylist(
            id = "pl_bass_monster",
            title = "YouTube Music: Ultra Bass & EDM",
            description = "Diepe 30Hz-80Hz sub-basslijnen en hardstyle beats voor maximale driver-excursie.",
            genre = "Electronic / Bass",
            trackCount = 45,
            recommendedPreset = "YouTube Music Bass Monster",
            queryUrl = "https://music.youtube.com/search?q=EDM+Bass+Boosted+Hi-Res",
            colorStartHex = 0xFFFF0055,
            colorEndHex = 0xFF7A00FF
        ),
        YtPlaylist(
            id = "pl_philips_audiophile",
            title = "Audiophile Master Series",
            description = "Ongecomprimeerde akoestische dynamiek, brede soundstage en microscopisch detail.",
            genre = "Hi-Res Audiophile",
            trackCount = 30,
            recommendedPreset = "Philips TAH6519 Pro ANC Tuning",
            queryUrl = "https://music.youtube.com/search?q=Audiophile+Acoustic+Master+FLAC",
            colorStartHex = 0xFF00E5FF,
            colorEndHex = 0xFF0044FF
        ),
        YtPlaylist(
            id = "pl_dutch_top",
            title = "Top Hits Nederland 2026",
            description = "De populairste tracks in Nederland met gebalanceerde vocale presentatie.",
            genre = "Dutch Pop & Rap",
            trackCount = 50,
            recommendedPreset = "Vocal & Acoustic Clarity",
            queryUrl = "https://music.youtube.com/search?q=Top+50+Nederland",
            colorStartHex = 0xFFFF9900,
            colorEndHex = 0xFFFF0033
        ),
        YtPlaylist(
            id = "pl_spatial_8d",
            title = "3D & 8D Spatial Audio Experience",
            description = "Ronddraaiend binauraal geluid; vereist stereo Bluetooth hoofdtelefoon!",
            genre = "Spatial / 8D",
            trackCount = 28,
            recommendedPreset = "Live Concert & Spatial 3D",
            queryUrl = "https://music.youtube.com/search?q=8D+Audio+Spatial+Surround",
            colorStartHex = 0xFF00F5A0,
            colorEndHex = 0xFF0072FF
        ),
        YtPlaylist(
            id = "pl_lofi_relax",
            title = "Lo-Fi Beats & Relax Focus",
            description = "Zachte analoge warmte en lofi drums voor ontspanning en geconcentreerd werken.",
            genre = "Lo-Fi / Chill",
            trackCount = 60,
            recommendedPreset = "Night Chill & Lo-Fi Relax",
            queryUrl = "https://music.youtube.com/search?q=Lofi+hip+hop+radio+beats+to+relax",
            colorStartHex = 0xFF7F00FF,
            colorEndHex = 0xFFE100FF
        ),
        YtPlaylist(
            id = "pl_rock_classics",
            title = "Rock & Alternative Energy",
            description = "Krachtige gitaarriffs, drumsolo's en dynamische zang met hoge attack.",
            genre = "Rock / Metal",
            trackCount = 40,
            recommendedPreset = "Rock & Metal Punch",
            queryUrl = "https://music.youtube.com/search?q=Rock+Classics+Remastered",
            colorStartHex = 0xFFFF416C,
            colorEndHex = 0xFFFF4B2B
        )
    )

    val FEATURED_TRACKS = listOf(
        YtTrack(
            id = "t1",
            title = "Get Lucky (Hi-Res Remaster)",
            artist = "Daft Punk ft. Pharrell",
            genre = "Disco Funk",
            duration = "4:08",
            recommendedPreset = "Philips TAH6519 Pro ANC Tuning",
            directUrl = "https://music.youtube.com/search?q=Daft+Punk+Get+Lucky",
            badge = "Master 96kHz"
        ),
        YtTrack(
            id = "t2",
            title = "Blinding Lights",
            artist = "The Weeknd",
            genre = "Synthwave / Pop",
            duration = "3:20",
            recommendedPreset = "YouTube Music Bass Monster",
            directUrl = "https://music.youtube.com/search?q=The+Weeknd+Blinding+Lights",
            badge = "Bass Boost"
        ),
        YtTrack(
            id = "t3",
            title = "Hotel California (Live Acoustic 1994)",
            artist = "Eagles",
            genre = "Acoustic Live",
            duration = "6:54",
            recommendedPreset = "Live Concert & Spatial 3D",
            directUrl = "https://music.youtube.com/search?q=Eagles+Hotel+California+Live+Acoustic",
            badge = "Spatial 3D"
        ),
        YtTrack(
            id = "t4",
            title = "Europapa",
            artist = "Joost Klein",
            genre = "Happy Hardcore / Pop",
            duration = "2:40",
            recommendedPreset = "Electronic & EDM Energy",
            directUrl = "https://music.youtube.com/search?q=Joost+Europapa",
            badge = "High Energy"
        ),
        YtTrack(
            id = "t5",
            title = "Stairway to Heaven (Remastered)",
            artist = "Led Zeppelin",
            genre = "Classic Rock",
            duration = "8:02",
            recommendedPreset = "Audiophile Harman Target",
            directUrl = "https://music.youtube.com/search?q=Led+Zeppelin+Stairway+to+Heaven",
            badge = "Audiophile"
        )
    )
}
