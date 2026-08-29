# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Functies
- 10-band equalizer + custom presets (Room)
- Favoriete en recente presets (snelbalk)
- Luister-scenes (pendelen, sport, nacht, fiets, bibliotheek, vergadering, rust, trein, koken, kids)
- Auto-scene op tijdstip
- Veilig volume (70%) + nachtwacht na 22:00
- Crossfeed, mono-mix, EQ-lock, A/B-vergelijking
- Slaaptimer 15/30/60/90/120 min met fade-out
- Luisterdosis-tracker met pauzewaarschuwing
- Gehoortest → L+R gemiddelde EQ-compensatie + echte score
- Gemini AI akoestische tuner
- YouTube Music web/native
- EQ delen / importeren via klembord of Android share-sheet
- **Backup/restore van alle custom presets als één JSON** (balk: Backup presets / Herstel backup)
- **Per-app EQ** (Spotify vs YT Music vs andere spelers; knop “EQ voor …”)
- BT-headset detectie + codec hints (LDAC / aptX Adaptive)
- Now Playing auto-EQ (genre uit YT Music / Spotify / etc.)
- Adaptieve loudness op basis van mediavolume
- Quick Settings-tegel: DSP aan/uit
- Quick Settings-tegel: scene wisselen
- Persistente DSP-notificatie (pauzeer / start / volgende scene / sluit)
- Homescreen-widget: DSP toggle + volgende scene
- Zoek koptelefoon (L/R pieptonen)
- Headset-batterij in de statusbalk (als Android die vrijgeeft)
- Auto-pauze DSP bij Bluetooth-disconnect (chip: BT-pauze)
- Weekdosis naast dagelijkse luisterminuten
- EQ-geheugen per headset (laatste curve wordt per BT-naam bewaard en hersteld)
- Stereo-breedte / spatializer-slider (0 mono → 200 brede soundstage)
- Per-app EQ-overzicht: toepassen, wissen, aan/uit

## Volgende verbeteringen
- Wear OS-tegel voor scene + DSP
- Automatisch LDAC-bitrate op echte RSSI i.p.v. simulatie
- Hardware Spatializer (Android 13+) koppelen naast Virtualizer

## Bouwen
Android Studio + JDK 17. Open de root en sync Gradle.
API-sleutel voor Gemini: zie `.env.example`.
