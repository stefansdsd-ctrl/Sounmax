# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-14 — batch 35)
- Glance-hoofdwidget: scene, ANC, DSP, undo, slaaptimer, suggestie.
- Statusregel met headset + telefoonaccu en **STIL** tijdens stille uren.
- RemoteViews-hoofdwidget blijft staan tot je de Glance-versie pinnet.

## Nieuw (2026-09-14 — batch 34)
- Glance-favorietenwidget met 4 scenes + huidige suggestie.
- QS-tegel **Suggestie** past de slimme scene toe.
- Suggestie gebruikt eerst jouw uur-statistiek, daarna weer/circadiaan.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Volgende software
- RemoteViews-hoofdwidget uitfaseren
- Wear UI: vorige/volgende scene + slaaptimer-chip
- Echte multipoint-payload na TAH6519-dump

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
