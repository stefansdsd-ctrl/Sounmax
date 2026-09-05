# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: Regen, Spits, Thuisavond, Nachttrein, Intercity, Koffietent, Huisarts, Ziekenhuis, Thuis+kids, Regenfiets
- Auto-scene: Buienradar/KNMI → regen; 9292/vertrektijden → spits
- Adaptive EQ-hints voor regen, spits, thuisavond en de nieuwe scenes
- Extra scenes zichtbaar in groepenfilter (Onderweg / Dag / Nacht)

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Wear-complicatie voor 1-tap scene
- Crossfeed-bestanden controleren op main

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
