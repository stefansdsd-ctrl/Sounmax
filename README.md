# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: Deep work, HIIT, Live sport
- Weekdosis (WHO-achtige pauzes bij 10 uur/week)
- Accu ≤20%: automatisch LDAC 330 kbps (verbinding sparen)
- Fletcher–Munson loudness-contour
- Echte crossfeed + safe limiter via DynamicsProcessing
- Spraak-boost op podcast/call/kantoor-scenes
- Scene één oor schakelt nu echte mono
- Pauze BT: muziek pauzeert bij disconnect, speelt door bij reconnect
- Adaptive EQ: K-pop, afrobeat/amapiano, nederpop

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
