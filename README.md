# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- **Eén-tik profielen** (`OneTapProfiles`): Woon-werk, Focus, Slaap, Buiten, Gesprek, Uit — scene + ANC + volume-cap
- **Echte ANC-modes** (`RealAncController`): hardware ANC/Normaal/Transparantie + soft-EQ; leert UUID/payload
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

## ANC (TAH6519)
Handleiding-knop: **ANC ↔ Normaal ↔ Transparantie**.

| App-modus | Hardware | Soft-EQ |
|-----------|----------|---------|
| OFF | Normaal | passief |
| STRONG / ADAPTIVE / WIND_GUARD | ANC | ja (adaptief/wind) |
| AMBIENT | Transparantie | spraak-boost |

Gebruik `SoftwareAnc.applyWithHardware(context, mode)`.
Eerste geslaagde GATT-write wordt opgeslagen (`soundmax_anc_hw`).
Zonder dump blijft soft-ANC altijd actief.

## Eén-tik
`OneTapProfiles.apply(context, "commute"|"focus"|"sleep"|"outdoor"|"talk"|"off")`

## Wifi-fingerprint
`WifiRssiMap.pinCurrent(context, sceneId, label)` op een plek; auto-match in fusie (gewicht 9).

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
