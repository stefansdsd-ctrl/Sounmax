# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes Code + Cardio; Recap/Wind/Piano/Akoestisch nu ook in ALL + groepen
- Auto-scene: Bandcamp, Castbox, Podcast Addict, Apple Podcasts
- Adaptive EQ: akoestisch + cardio
- Oorpauze: melding + sessie reset na 50 min (gelijkgetrokken)
- Auto-scene volgt Now Playing (podcast/film/game/genre), weer/vlucht/accu blijven leidend
- Extra podcast-apps: Podcast Addict, Castbox
- Scenes: Wind, Fiets-regen, Interview, Mix-check, Vlucht-slaap, Piano, Akoestisch, Recap
- Weer: spitstijd + regen → Pendelen regen; buiten + wind → Wind
- Recente scenes worden onthouden
- QS-tegel Oorpauze (scene Rust)
- Adaptive EQ voor piano/akoestisch
- Widget/volume-cyclus gebruikt favorieten als er ≥2 zijn
- Dubbel volume-omhoog = volgende scene, omlaag = vorige
- Auto-oorpauze na 50 min ononderbroken (widget-tick)
- Head-track kalibratie: kijk recht vooruit → nulpunt voor yaw/pitch
- Wear-complicatie tik wisselt naar de volgende scene
- Auto veilig-volume vanaf 180 min vandaag
- Auto-Saver bij headset-accu ≤15%

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
