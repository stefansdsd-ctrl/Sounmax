# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-13 — batch 21)
- Widget: ANC-cycle knop (geen Activity).
- Scene-mappen: lang-druk naar links, dubbeltik naar rechts.
- Wear: Focus, Undo, Suggestie-knoppen.

## Nieuw (2026-09-13 — batch 20)
- App-mix chip: volume-cap + per-app EQ in één rij (Meer).
- Widget-Focus tikt 25 → 45 → 90 min, daarna uit.

## Nieuw (2026-09-13 — batch 19)
- Per-app volume-cap: Meer → App-vol, tap cyclus 50/60/75/90/100% per speel-app.
- Scene-mappen: lang indrukken schuift map naar links (volgorde blijft bewaard).
- Widget-actie `FOCUS_25` start 25 min Deep work zonder Activity.

## Nieuw (2026-09-13 — batch 18)
- Widget: ANC-cycle + volgende favoriet, zonder Activity.
- Multipoint-chip: wissel A2DP-sink (telefoon-kant).
- Volume-ramp bij Bluetooth-reconnect (~800 ms).
- Haptic-intensiteit: uit / zacht / normaal / sterk.
- Scene-mappen: Werk / Onderweg / Nacht / … filterchips.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- Wear-complication UI-module (data-pad klaar)

## Volgende software (batch 22)
- Glance-widget i.p.v. RemoteViews
- Headset-multipoint via vendor GATT
- Wear-complication UI-module

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
