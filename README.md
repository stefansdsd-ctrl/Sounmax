# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Genre-EQ-balk: herkent nummer/artiest/genre uit alle Now Playing-apps en past 10-bands + bass/clarity toe
- Zoek headset: live RSSI-ring (dichtbij → zwak) naast laatste locatie op de kaart
- Now Playing leest extra metadata (compilation/subtitle) voor betere genre-match
- QS Combo-tegel: één tik start Focus 25 + Focus-scene + ANC Maximaal
- SAF bestandskiezer: backup opslaan/openen als JSON (naast klembord)
- Lokale snapshot-herstel vanuit BackupBar
- Widget: tweede rij Undo + Focus 25
- DSP-notificatie: Focus 25 toggle + resterende minuten
- Soft-ANC adaptief: ruisvloer uit volume/activiteit/RSSI
- Software-ANC gekoppeld aan `setAncMode` + bass-delta
- WHO-luisterdosis: 80 dB × 40 u, dag+week, pauze-advies
- Auto-modus fusie: activiteit + agenda + plaats + weer
- Headset-profiel per MAC, gespreksboost, undo, scene-crossfade
- Wear: Focus 25, undo, scene-lock + schema
- Geofence, wifi-plaats, pendel, vliegtuigmodus, RSSI→LDAC 330

## Betere volgende functies
1. Optionele mic-RMS ruisvloer (RECORD_AUDIO)
2. Drive-backup van presets (OAuth in-app)
3. Wear-complicatie status-sync extra betrouwbaar
4. Echte Philips ANC-GATT (dump nodig)
5. Auto-apply genre-EQ zonder tik (opt-in)
6. Indoor-positie via wifi-RSSI-kaart

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
