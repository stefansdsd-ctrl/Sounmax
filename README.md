# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Undo-tegel + Undo in DSP-notificatie
- Scene-wissel slaat vorige scene op (`prev_scene_id`)
- Genre-EQ: album + extra metadata (niet alleen YT Music)
- Scene-EQ crossfade 600 ms
- Wear: Focus 25, undo, scene-lock + schema
- Werk/weekend-pin, schema-uren, agenda pre-roll
- Geofence, wifi-plaats, pendel, vliegtuigmodus
- RSSI → LDAC 330 bij zwak signaal

## Betere volgende functies
1. Soft-ANC via mic-ruisvloer (zonder GATT)
2. Auto-modus: activiteit + agenda + plaats + weer samen
3. Gespreksboost / sidetone-sim
4. Headset-profiel per MAC (TAH vs andere)
5. Drive-backup van presets
6. Wear-complicatie status-sync
7. Echte Philips ANC-GATT (dump nodig)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
