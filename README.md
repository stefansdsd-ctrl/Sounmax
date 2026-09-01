# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes School, Strand, Ziekenhuis, Station, Kerk
- Weekend-middag suggereert Strand i.p.v. Film
- Doordeweeks 14–16 uur: School/studie
- App-detectie Tidal, Deezer, SoundCloud, Amazon Music, Apple Music
- Recente scenes (↻) na favorieten, weekend-suggestie via suggestedNow
- Weekdosis in wellness-balk + 45-min pauze-waarschuwing
- EQ-ongedaan-maken (chip **EQ ⌫**)
- Recente scenes eerst (na favorieten)
- Scene per headset onthouden
- Scenes Bus + Tram
- App-detectie: WhatsApp, Libby, HBO/Max
- EQ A/B: chips **EQ A** / **EQ B** opslaan, **A↔B** wisselen
- Adaptive EQ per nummer/genre (chip **Adapt. EQ**); scene-slot blijft leidend
- Scenes Luisterboek + Nieuws + Audible/Storytel-detectie
- Gehoorprofiel automatisch na herstart en bij headset-koppeling
- Schakelaar “Automatisch toepassen” op het gehoor-tabblad
- Loudness auto: extra bas/loudness bij zacht volume (Fletcher-Munson)
- App-scene-chip: EQ volgt Spotify/podcast/game/call
- Auto-scene in de auto (BT-naam BMW/Tesla/carkit/…)
- Wear: play/pauze + volume ±
- Dosis op Wear-status + DSP-notificatie
- Na 2 uur luisteren: veilig volume automatisch aan
- Laatste headset-locatie + kaart (GPS bij verbinding)
- Nabijheid via RSSI (dichtbij / verder weg)
- Accu-spaarstand bij headset ≤15%: minder loudness/spatial
- Locatie-permissies voor weer + vind-headset
- Wear-complicatie: scene + batterij op het wijzerplaatvlak
- Wear Ambient-modus (dim-UI, geen extra knoppen)
- Wear-knop **Vind headset** (L/R piep op de telefoon)
- Stille uren 22:00–07:00: veilig volume automatisch
- Head-tracking spatializer (yaw → balans, pitch → virtualizer)
- QS Spatial-cyclus: uit → spatial → head-track
- Wear OS-tegel: DSP, ANC-cyclus, volgende scene, slaaptimer, play/pauze
- Wear-app: ANC, spatial, haptic click
- Widget- en notificatieknop **Nu**: past de tijdsuggestie meteen toe
- Auto-scene elk uur (als slot uit is)
- Weer-chip + Weer Nu in de wellness-balk (Open-Meteo + SceneController)
- Weer/GPS-scene: regen, wind, buiten via Open-Meteo
- Boot-receiver: DSP-service + schema na herstart
- GATT-services ontdekken + dump delen (voor echte ANC-UUIDs)
- Scene-zoekbalk
- Stille uren instelbaar (22–7 / 23–8 / 21–6 / uit)
- Week-dosis in wellness-balk

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Sleep-timer fade-out naar systeemvolume (nu alleen DSP-loudness)
- Per-oor EQ vanuit gehoortest L/R

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
