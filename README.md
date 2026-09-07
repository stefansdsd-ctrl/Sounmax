# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Genre-EQ-sterkte 25–100% (`GenreEqStrength.setPercent`) — offsets zonder stapeling
- Auto genre-EQ (opt-in): volgt nieuw nummer vanaf vaste baseline
- Genre-EQ-balk: herkent nummer/artiest/genre en past 10-bands + bass/clarity toe
- Zoek headset: live RSSI-ring naast laatste locatie
- QS Combo-tegel: Focus 25 + Focus-scene + ANC Maximaal
- SAF backup JSON, snapshot-herstel, widget Undo + Focus 25
- Soft-ANC, WHO-luisterdosis, auto-modus fusie, Wear Focus 25

## Betere volgende functies
1. UI-slider koppelen aan `GenreEqStrength.setPercent`
2. Optionele mic-RMS ruisvloer (RECORD_AUDIO)
3. Drive-backup van presets
4. Wear-complicatie status-sync
5. Echte Philips ANC-GATT (dump nodig)
6. Indoor-positie via wifi-RSSI-kaart

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
