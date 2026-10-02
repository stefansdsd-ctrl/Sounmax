# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-02 — batch 245)
- Los-pauze: headset eruit pauzeert media via ACTION_AUDIO_BECOMING_NOISY (chip Los).
- Headset-accu: percentage uit Bluetooth BATTERY_LEVEL_CHANGED, geen GATT nodig (chip Accu).
- Slot-cap en stil-cap worden nu echt toegepast vanuit WhyNow.

## Nieuw (2026-10-02 — batch 244)
- Slot-cap: vergrendelscherm + muziek boven 72% → 54%.
- Stil-cap: stille beltoon + muziek boven 64% → 46%.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
