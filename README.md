# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes Ochtend, Bass-check, Stereo-test, Referentie, Speaker
- Werkdag 06–08 suggereert Ochtend
- Auto-Saver bij headset-accu ≤15%
- Weekdosis-label (rust na 600 min)
- Auto-scene: fitness-apps → Gym, navigatie → Pendelen
- Extra podcast/luisterboek-apps + Bandcamp-detectie

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
