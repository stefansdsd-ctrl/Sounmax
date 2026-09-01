# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- SceneController hersteld (compile + auto-scene, dosis, weer, Now Playing)
- Scenes Lo-fi, Klassiek, Deep work, Reizen, Accu-spaar, Live sport, Werkplaats
- Scene-zoekbalk + GATT-balk in de hoofd-UI
- App-detectie Bandcamp/Pandora + live-sport apps
- Accu ≤12% schakelt Accu-spaar (slot uit)
- Scenes Meditatie, Restaurant, Luchthaven, Bioscoop
- Spotify/YouTube Music: scene volgt genre i.p.v. altijd Party

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Echte per-kanaal DSP (DynamicsProcessing) i.p.v. alleen EQ-curve + balans

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
