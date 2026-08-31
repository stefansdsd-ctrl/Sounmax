# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Wear OS-tegel met knoppen: DSP, volgende scene, slaaptimer
- Wear-app: extra slaaptimer-knop + snellere status-refresh
- Data Layer: DSP aan/uit, scene ±, slaaptimer, batterij
- Widget- en notificatieknop **Nu**: past de tijdsuggestie meteen toe
- Auto-scene elk uur (als slot uit is)
- Boot-receiver: DSP-service + schema na herstart

## Volgende
- Philips ANC via echte GATT-UUIDs
- Head-tracking spatializer verfijnen
- Wear: haptic feedback + ANC-knop

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
