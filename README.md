# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-10h)
- **CallModeGuard**: tijdens bel → gesprek/transparency + speech-EQ, daarna vorige scene.
- **BtDisconnectPause**: media pauze bij ACL-disconnect.
- **FindHeadset**: chirp om de koptelefoon te vinden.
- **WeeklyListenReport**: 7-daagse luisterminuten + hint.

## Nieuw (2026-09-10g)
- **SleepTimerBar**: 15/30/45/60/90 min in de app (fade + pauze).
- **BalanceMonoBar**: L/R-balans + mono-mix, onthouden.
- **Scene-lock chip**: auto-scene tijdelijk vastzetten.

## Nieuw (2026-09-10f)
- **LowBatteryAncSaver**: ANC automatisch uit onder 15% headset-accu, terug boven 25%.
- **Widget**: codec + ANC-kortlabel naast accu/DSP.

## Nieuw (2026-09-10e)
- **SceneUsage**: telt scene-gebruik per uur; suggestie “Nu: …” op basis van jouw patroon.
- **SmartSuggestBar**: één tik naar de meest waarschijnlijke scene.
- **NightVolumeGuard**: tussen 22:00–07:00 volume-cap 50% (uit te zetten).

## Nieuw (2026-09-10d)
- **CommuteHint** in auto-scene-loop + pendel-chip (SSID/activiteit).
- **Wear**: weekdosis + pauze-hint, swipe links/rechts voor scene, OLED-contrast.
- **Spatial horizon**: yaw/pitch-stip.

## Nieuw (2026-09-10c)
- **EQ-fade live** in `AudioDspManager.applyPreset`.
- **ListenDose ticker** elke minuut bij volume > 0.
- **SceneSearch** in scene-chips + **SceneHistory** undo.
- **CommuteHint**: NS/GVB/wifi + activity → trein/metro/fiets/auto.

## Nieuw (2026-09-10b)
- **Scene-zoek + tags**, wifi-hysteresis 90s, luisterdosis, EQ-fade.

## Nieuw (2026-09-10)
- Scene-historie + undo, LDAC-waarschuwing, per-app EQ-sterkte, Drive-backup, crash-log, AI-cache.

## Al aanwezig
- EQ A/B, SceneShare, Wear ANC-tegel, widgets, QS-tegels
- OLED-thema, ANC-haptic, head-track kalibratie
- Kamer-wifi-pins, multipoint, nRF GATT-dump, RealAncController
- Veilig volume 60% cap

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- CallModeGuard / FindHeadset in MainActivity bedraden

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
