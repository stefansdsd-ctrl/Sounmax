# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-13 — accu-ETA + vergadering + kindveilig)
- Home-chip accu-ETA (~Xu Ym) uit drain-curve; korter bij ANC + LDAC.
- Chip Vergadering: pauze + talk-through + ConversationBoost, 15/30/60 min, daarna vorige scene.
- Agenda-hint 5 min voor event (alleen met calendar-recht).
- Kindveilig achter Meer: volume-cap + pincode.
- Head-off pause (OffEarPause) aanwezig in media-laag.

## Nieuw (2026-09-13 — focus-chip + duur)
- Home-chip “Focus” met 25 / 45 / 90 min (Deep work + DND + scene-lock).
- Reis-chip ook op home, zelfde duur-keuze.
- Laatste duur wordt onthouden.

## Nieuw (2026-09-13 — reis-lock + weekdeel + vliegtuig)
- Chip “Reis”: ANC sterk + pendel-profiel, auto-scene 45 min vast.
- Handmatige scene zet nu écht `manual_scene_until` +15 min.
- Weekdosis delen via share-sheet; one-tap “Vliegtuig”.

## Nieuw (2026-09-13 — pasvorm + handmatige scene-hold)
- Chip “Pasvorm”: 3 mic-RMS samples → seal-score (geen GATT).
- Handmatige scene blokkeert auto-wissel 15 min; chip “Auto uit Xm” heft op.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- Wear-complication UI-module (data-pad klaar)
- Glanceable widget-acties: ANC-cycle / volgende favoriet / Focus 25

## Volgende software (batch 18)
- Widget-acties zonder full Activity
- Multipoint-chip (telefoon ↔ laptop)
- Volume-ramp bij reconnect
- Haptic-intensiteit instelbaar
- Scene-mappen (werk / reis / nacht)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
