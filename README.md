# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-02 — batch 246)
- Accu-cap: headset ≤18% + muziek boven 60% → 42% (chip Accu).
- Terug-play: na los-pauze hervat media als de headset binnen 3 min terugkomt (chip Terug).

## Nieuw (2026-10-02 — batch 245)
- Los-pauze: headset eruit pauzeert media via ACTION_AUDIO_BECOMING_NOISY (chip Los).
- Headset-accu: percentage uit Bluetooth BATTERY_LEVEL_CHANGED, geen GATT nodig (chip Accu).
- Slot-cap en stil-cap worden nu echt toegepast vanuit WhyNow.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
