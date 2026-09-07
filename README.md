# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scene-suggestie op ruisvloer + activiteit (`SceneNoiseSuggest`, stem in AutoModeFusion)
- UI-chip **Ruis-tip** + reden in statusregel (`last_noise_suggest_reason` + fusie)
- Genre-EQ-sterkte-slider 25 / 50 / 75 / 100% (persistent)
- Auto genre-EQ (opt-in): volgt nieuw nummer vanaf vaste baseline
- Genre-EQ-balk: herkent nummer/artiest/genre en past 10-bands + bass/clarity toe
- Zoek headset: live RSSI-ring naast laatste locatie
- QS Combo-tegel: Focus 25 + Focus-scene + ANC Maximaal
- SAF backup JSON, snapshot-herstel, widget Undo + Focus 25
- Soft-ANC, WHO-luisterdosis, auto-modus fusie, Wear Focus 25

## Betere volgende functies
1. Optionele mic-RMS ruisvloer (RECORD_AUDIO, opt-in)
2. Drive-backup van presets
3. Wear-complicatie status-sync
4. Echte Philips ANC-GATT (dump TAH6519 nodig)
5. Indoor-positie via wifi-RSSI-kaart

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
