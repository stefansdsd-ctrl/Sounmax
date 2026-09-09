# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- **EQ A/B**: sla twee curves op (EQ A / EQ B) en wissel live (`EqSnapshot`)
- **Plak-chip**: SceneShare-JSON vanaf klembord in de scenes-balk
- **Wear ANC-tegel**: compacte tegel, tik wisselt modus (`SounmaxAncTileService`)
- **Live codec in widget/Wear**: LDAC/aptX/AAC + bitrate (kbps) naast accu
- **Scene-plak**: chip “Plak” importeert SceneShare-JSON vanaf klembord (`SceneShare.importFromClipboard`)
- **Share-intent**: `ACTION_SEND` text/plain met scene-JSON activeert de scene
- **OLED-thema**: Compose zwart-schema + chip “OLED”
- **Scene-delen per chip**: JSON via `SceneShare`
- **Accu-historie widget**: sparkline + ETA
- **ANC-haptic** (`AncHaptics`): korte tril per modus, uit te zetten
- **OLED-zwart vlag** (`AncHaptics.KEY_OLED`)
- **Head-track kalibratie-offset** (`HeadTrackCalib` + bestaande `HeadTracker.calibrateNeutral()`)
- **Kamer-wifi-pins**: woonkamer, keuken, slaapkamer, studeerkamer, kantoor, sportschool, koffie, trein (max 48 fingerprints)
- **One-tap extra**: Sport, Trein, Café
- **Multipoint-wisselen** (`MultipointSwitcher`)
- **nRF-stijl GATT-dump** (`NrfStyleGattDump`)
- **Eén-tik profielen** + Wear-tegel
- **Echte ANC-modes** (`RealAncController`)
- **ANC-knop leren**: GATT-notify vangen i.p.v. payload-gokken (`AncNotifyLearner`)
- **Veilig volume**: cap 60% STREAM_MUSIC als vlag/stille uren aan staan

## nRF-dump (TAH6519)
1. Headset verbinden
2. Dump baseline, druk ANC-knop, dump opnieuw
3. Vergelijk `Value:`-regels

## ANC
| App-modus | Hardware | Soft-EQ |
|-----------|----------|---------|
| OFF | Normaal | passief |
| STRONG / ADAPTIVE / WIND_GUARD | ANC | ja |
| AMBIENT | Transparantie | spraak-boost |

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
