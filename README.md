# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Fletcher–Munson loudness-contour (meer bas/lucht bij zacht volume)
- Echte crossfeed + safe limiter via DynamicsProcessing
- Spraak-boost op podcast/call/kantoor-scenes
- Scene één oor schakelt nu echte mono
- Scenes: Deep work, HIIT, Live sport

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
