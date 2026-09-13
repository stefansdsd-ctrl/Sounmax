# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-13 — batch 24)
- Scene-mappen: sleep na lang-druk om te sorteren (blijft bewaard).
- Dubbeltik op **Alles** herstelt de standaard-volgorde.
- Lang-druk / dubbeltik blijft werken als stap-links / stap-rechts.

## Nieuw (2026-09-13 — batch 23)
- Vendor-GATT multipoint-probe (`VendorMultipoint`) bij service-discovery.
- Widget-knop MP: wissel volgende A2DP-sink (`MultipointSwitcher.cycleNext`).

## Nieuw (2026-09-13 — batch 22)
- Widget-ANC knop écht aangesloten (STRONG → MILD → OFF → AWARE…).
- Widget-meta: ANC + codec + DSP-status.
- Wear-complication toont scene, %, ANC en codec.

## Nieuw (2026-09-13 — batch 21)
- Widget: ANC-cycle knop (geen Activity).
- Scene-mappen: lang-druk naar links, dubbeltik naar rechts.
- Wear: Focus, Undo, Suggestie-knoppen.

## Nieuw (2026-09-13 — batch 20)
- App-mix chip: volume-cap + per-app EQ in één rij (Meer).
- Widget-Focus tikt 25 → 45 → 90 min, daarna uit.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Volgende software (batch 25)
- Glance-widget i.p.v. RemoteViews
- Echte multipoint-payload na TAH6519-dump
- Verborgen scene-mappen (hide unused)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
