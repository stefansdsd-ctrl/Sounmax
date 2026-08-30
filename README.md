# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Wear OS-module (`:wear`): tegel + watch-app
- Data Layer: DSP aan/uit, scene ±, slaaptimer, batterij
- Widget- en notificatieknop **Nu**: past de tijdsuggestie meteen toe
- Auto-scene elk uur (als slot uit is)
- Boot-receiver: DSP-service + schema na herstart

## Volgende
- Wear-tegel met knoppen (nu vooral status + app)
- Philips ANC via echte GATT-UUIDs
- Head-tracking spatializer verfijnen

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
