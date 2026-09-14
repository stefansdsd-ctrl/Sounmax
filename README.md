# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-14 — batch 32)
- `QuietHoursBar`: venster cyclen (22–7 / 23–8 / 21–6 / uit).
- Bedtime-arm past scene **Slaap** toe + stille uren + fade.
- Home-widget meta toont **STIL** tijdens stille uren (volgt in widget-commit).

## Nieuw (2026-09-14 — batch 31)
- Wear-tile: vorige scene.
- QS scene-tile toont `BT xx%`.
- `SleepTimer` koppelt aan `SleepFade` (fade laatste minuut).
- `ListenCap`: weekdosis-cap verlaagt volume zacht.

## Nieuw (2026-09-14 — batch 30)
- `ReconnectScene`: laatste scene klaar bij headset-reconnect.
- `SleepTimer`: 1–180 min, stop via callback.
- Roadmap in `FEATURES.md` (Glance, Wear-scene, QS-tile, gehoor-cap).

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Volgende software (batch 33)
- Glance-widget i.p.v. RemoteViews
- Echte multipoint-payload na TAH6519-dump

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
