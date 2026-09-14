# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-14 — batch 26)
- HiddenScenes in folder-chips: verborgen mappen verdwijnen uit de rij.
- Chip **Toon verborgen** / **Verberg extra**.
- Lang-druk op een map (niet Alles/Favorieten) verbergt of toont die map.

## Nieuw (2026-09-13 — batch 25)
- Verborgen scene-mappen: `HiddenScenes` (hide/show/toggle, hideUnused).
- Alles en Favorieten blijven altijd zichtbaar.
- UI: bind chips aan `HiddenScenes.visibleLabels(context)` + lang-druk hide.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Volgende software (batch 27)
- Glance-widget i.p.v. RemoteViews
- Echte multipoint-payload na TAH6519-dump
- Phone-accu alert naast headset LowBatteryAncSaver
- Wear-complication UI verfijnen

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
