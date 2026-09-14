# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-14 — batch 27)
- Telefoon-accu alert (≤15%, niet laden): melding + toast, max 1×/3u.
- `PhoneBatteryAdvisor.charging()` — saver/LDAC-330 alleen zonder lader.
- Wear-status: `phone_battery` naast headset-accu.
- DSP-service toont telefoon-% in de foreground-notificatie.

## Nieuw (2026-09-14 — batch 26)
- HiddenScenes in folder-chips: verborgen mappen verdwijnen uit de rij.
- Chip **Toon verborgen** / **Verberg extra**.
- Lang-druk op een map (niet Alles/Favorieten) verbergt of toont die map.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Volgende software (batch 28)
- Glance-widget i.p.v. RemoteViews
- Echte multipoint-payload na TAH6519-dump
- Wear-complication UI met phone+headset accu

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
