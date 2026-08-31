# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Head-tracking spatializer (yaw → balans, pitch → virtualizer)
- QS Spatial-cyclus: uit → spatial → head-track
- Wear-knop Spatial / Head-track
- Wear OS-tegel: DSP, ANC-cyclus, volgende scene, slaaptimer
- Wear-app: ANC-knop + haptic click bij elke actie
- Data Layer: DSP, scene ±, slaaptimer, batterij, ANC, spatial
- Widget- en notificatieknop **Nu**: past de tijdsuggestie meteen toe
- Auto-scene elk uur (als slot uit is)
- Boot-receiver: DSP-service + schema na herstart

## Volgende
- Philips ANC via echte GATT-UUIDs
- Wear: complicatie + Ambient-modus

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
