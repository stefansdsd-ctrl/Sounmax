# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
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
2. Crossfeed + stereo-breedte (DSP)
3. Slaaptimer met fade-out + volume-cap per scene
4. Wear-complicatie: scene + batterij + Focus
5. Laatste scene/EQ per headset-MAC
6. Wind-scene: lopen + hoge mic-RMS
7. Echte Philips ANC-GATT (dump TAH6519 nodig)
8. Indoor-positie via wifi-RSSI-kaart

## Mic-RMS
Zet de switch **Mic-RMS ruisvloer** aan (of `mic_rms_enabled=true` in prefs `scene_automation`).
App vraagt RECORD_AUDIO pas bij aanzetten. Korte ~80 ms VOICE_RECOGNITION-sample, max 1x per 8 s. Geen persistente opname.

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
