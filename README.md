# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- SceneController hersteld (scenes, lock, sleep-timer, GATT-dump delen)
- Accubesparing bij lage headset-accu + scene Saver
- Extra scenes: House, Techno, Dubstep, Ambient
- GATT: LE Audio / Hearing Access + vendor-FE hint
- Adaptive EQ: house / techno / dubstep

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
