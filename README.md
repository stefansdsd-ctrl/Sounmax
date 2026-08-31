# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Wear-complicatie: scene + batterij op het wijzerplaatvlak
- Wear Ambient-modus (dim-UI, geen extra knoppen)
- Wear-knop **Vind headset** (L/R piep op de telefoon)
- Stille uren 22:00–07:00: veilig volume automatisch
- Head-tracking spatializer (yaw → balans, pitch → virtualizer)
- QS Spatial-cyclus: uit → spatial → head-track
- Wear OS-tegel: DSP, ANC-cyclus, volgende scene, slaaptimer
- Wear-app: ANC, spatial, haptic click
- Widget- en notificatieknop **Nu**: past de tijdsuggestie meteen toe
- Auto-scene elk uur (als slot uit is)
- Weer/GPS-scene: regen, wind, buiten via Open-Meteo
- Boot-receiver: DSP-service + schema na herstart

## Volgende
- Philips ANC via echte GATT-UUIDs (GATT-dump nodig)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app wordt meegenomen via `wearApp(project(":wear"))`.
