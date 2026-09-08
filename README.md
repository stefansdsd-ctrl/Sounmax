# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- **Wind-detectie**: lopen/fietsen + hoge mic-RMS → WIND_GUARD-scene (`WindAdvisor`)
- **Stereo-breedte**: `StereoDynamics.stereoWidth(0..1.6)` (0=crossfeed, 1=normaal, 1.6=breed)
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
1. Persistente Drive-map via SAF tree-URI (geen OAuth)
2. UI-slider stereo-breedte koppelen aan `StereoDynamics.stereoWidth`
3. Slaaptimer fade bestaat; volume-cap per scene is nu in `SceneVolumeCap`
4. Wear-complicatie: scene + batterij + Focus
5. Laatste scene/EQ per headset-MAC bestaat (`HeadsetMemory`)
6. Wind-scene: gedaan (`WindAdvisor` + fusie-gewicht 11)
7. Echte Philips ANC-GATT (dump TAH6519 nodig)
8. Indoor-positie via wifi-RSSI-kaart

## Mic-RMS
Zet de switch **Mic-RMS ruisvloer** aan (of `mic_rms_enabled=true` in prefs `scene_automation`).
App vraagt RECORD_AUDIO pas bij aanzetten. Korte ~80 ms VOICE_RECOGNITION-sample, max 1x per 8 s. Geen persistente opname.

## Wind
Prefs `wind_detect_enabled` (default aan). Vereist mic-RMS + activity walk/bike/run.

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
