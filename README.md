# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Soft-ANC adaptief: ruisvloer uit volume/activiteit/RSSI (geen extra mic-permissie)
- Software-ANC gekoppeld aan `setAncMode` + bass-delta
- WHO-luisterdosis: 80 dB × 40 u, dag+week, pauze-advies
- Auto-modus fusie: activiteit + agenda + plaats + weer (gewicht)
- Headset-profiel per MAC (TAH vs andere), naam als fallback
- Gespreksboost / sidetone-sim (1–4 kHz tilt + limiter)
- Undo-tegel + Undo in DSP-notificatie
- Scene-wissel slaat vorige scene op (`prev_scene_id`)
- Genre-EQ: album + extra metadata (niet alleen YT Music)
- Scene-EQ crossfade 600 ms
- Wear: Focus 25, undo, scene-lock + schema
- Werk/weekend-pin, schema-uren, agenda pre-roll
- Geofence, wifi-plaats, pendel, vliegtuigmodus
- RSSI → LDAC 330 bij zwak signaal

## Betere volgende functies
1. Echte mic-RMS ruisvloer (RECORD_AUDIO, optioneel)
2. Drive-backup van presets
3. Wear-complicatie status-sync extra betrouwbaar
4. Echte Philips ANC-GATT (dump nodig)
5. Home-widget batterij + 3 favorieten afronden

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
