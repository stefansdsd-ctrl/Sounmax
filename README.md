# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Extra scenes zitten nu écht in ALL + groepen (niet alleen in een los bestand)
- Radio / TV / DJ-set / Liquid DnB
- Auto-scene herkent radio-apps, liquid funk, house/techno/dubstep/ambient
- Adaptive EQ voor liquid + radio

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
