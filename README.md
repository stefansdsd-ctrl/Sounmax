# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- **Wifi-RSSI-kaart**: indoor fingerprints (`WifiRssiMap.pinCurrent`) → scene via BSSID/RSSI-match
- **Vendor ANC-probe**: GATT dump + write-kandidaat voor Philips FE-services (`VendorAncProbe`)
- **Wind-detectie**: lopen/fietsen + hoge mic-RMS → WIND_GUARD-scene (`WindAdvisor`)
- **Stereo-breedte slider** op EQ-scherm (`stereo_width_pct`, 0–160%)
- **Persistente Drive-map** via SAF tree-URI (`Drive-map` / `Kies map`)
- **Scene volume-cap**: safeVolume-scenes max 60% STREAM_MUSIC, herstel bij normale scene
- UI-toggle **Mic-RMS ruisvloer** + runtime RECORD_AUDIO-prompt (`MicRmsBar`)
- Opt-in **mic-RMS ruisvloer** (`MicRmsProbe`, prefs `mic_rms_enabled`) — blend 70/30 met proxy
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
1. Persistente Drive-map: gedaan
2. Stereo-breedte slider: gedaan
3. Slaaptimer fade + scene volume-cap: gedaan
4. Wear-complicatie scene + batterij: gedaan
5. Headset-geheugen: gedaan
6. Wind-scene: gedaan
7. Echte Philips ANC-GATT: probe + dump klaar; echte mode-map na TAH6519 dump
8. Indoor wifi-RSSI-kaart: gedaan (`WifiRssiMap`)

## Mic-RMS
Zet de switch **Mic-RMS ruisvloer** aan (of `mic_rms_enabled=true` in prefs `scene_automation`).
App vraagt RECORD_AUDIO pas bij aanzetten. Korte ~80 ms VOICE_RECOGNITION-sample, max 1x per 8 s. Geen persistente opname.

## Wind
Prefs `wind_detect_enabled` (default aan). Vereist mic-RMS + activity walk/bike/run.

## Wifi-fingerprint
`WifiRssiMap.pinCurrent(context, sceneId, label)` op een plek; auto-match in fusie (gewicht 9).
Prefs `wifi_rssi_map` (default aan).

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
