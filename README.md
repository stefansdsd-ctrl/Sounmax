# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scene-favorieten: houd een scene ingedrukt (groep Favorieten)
- Scenes House, Techno, Dubstep, Ambient, Saver
- Adaptive EQ per genre (house/techno/dubstep)
- Accubesparing + sleep-timer + GATT-dump delen

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
