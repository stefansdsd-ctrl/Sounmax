# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-10c)
- **EQ-fade live** in `AudioDspManager.applyPreset`.
- **ListenDose ticker** elke minuut bij volume > 0.
- **SceneSearch** in scene-chips + **SceneHistory** undo.
- **CommuteHint**: NS/GVB/wifi + activity → trein/metro/fiets/auto.

## Nieuw (2026-09-10b)
- **Scene-zoek + tags**: `SceneSearch.query("trein")` filtert op naam, id, beschrijving en groep-tags.
- **Wifi-hysteresis 90s**: geen flikker café/trein bij korte SSID-wissel (`WifiPlaceAdvisor`).
- **Luisterdosis**: `ListenDose` houdt 7 dagen volume×minuten bij + pauze-hint boven 480 min-eq.
- **EQ-fade**: `EqFade.steps()` interpolateert 10 bands bij scene-wissel (~320 ms).

## Nieuw (2026-09-10)
- **Scene-historie + undo**: ringbuffer van 8 scene-id's (`SceneHistory`). `undo()` = vorige scene.
- **LDAC-waarschuwing**: bij LDAC + accu < 20% of RSSI < -75 dBm snackbar.
- **Per-app EQ-sterkte**: slider 0–100%, default 70%.
- **Backup Drive**: export/share-sheet / SAF-import.
- **Crash-log klembord** + **offline AI-cache** (laatste 3 Gemini-curves).

## Al aanwezig
- EQ A/B, SceneShare, Wear ANC-tegel, widgets, QS-tegels
- OLED-thema, ANC-haptic, head-track kalibratie
- Kamer-wifi-pins, multipoint, nRF GATT-dump, RealAncController
- Veilig volume 60% cap

## Nog open (hardware / UI-draad)
- Philips ANC-GATT bevestigen met dump op TAH6519
- CommuteHint koppelen in auto-scene loop (`SceneAutomation`)
- Wear-tegel: weekdosis + pauze-hint

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
