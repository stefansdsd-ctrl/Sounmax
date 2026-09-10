# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-10)
- **Scene-historie + undo**: ringbuffer van 8 scene-id's (`SceneHistory`). `undo()` = vorige scene.
- **LDAC-waarschuwing**: bij LDAC + accu < 20% of RSSI < -75 dBm snackbar “bitrate daalt — dichterbij of AAC” (`LdacWarn`, cooldown 90s).
- **Per-app EQ-sterkte**: slider 0–100%, default 70% (`AppEqMemory.strength` + `GenreEqStrength`).
- **Backup Drive**: `PresetBackup.exportToTree` / share-sheet / SAF-import.

## Al aanwezig
- EQ A/B, SceneShare-plak, Wear ANC-tegel, live codec in widget
- OLED-thema, ANC-haptic, head-track kalibratie
- Kamer-wifi-pins, multipoint, nRF GATT-dump, RealAncController
- Veilig volume 60% cap

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
