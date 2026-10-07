# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-07 — batch 293)
- DuckLane: alarm, bel en timer delen één volume-baseline.
- Laagste cap wint (wekker 28% boven bel 32% boven timer 36%).
- Herstel pas als niemand meer vasthoudt. Geen tweede verlaging meer bewaren.
- Oude per-duck `saved` blijft staan maar wordt niet meer als waarheid gebruikt.

## Nieuw (2026-10-07 — batch 292)
- Timer-duck: keukentimer of countdown terwijl muziek loopt → stapsgewijs naar 36%.
- Herkent sonification + notification. Niet ringtone, niet wekker.
- Na de piep +2 terug, geen knal. Wacht op Alarm-duck en Bel-duck.
- Chip Timer. Banner: Timer-duck actief (36%, piep hoorbaar).
- Zoeker: timer, kookwekker, countdown, piep, keuken.
- Alleen Android 8+.

## Nieuw (2026-10-07 — batch 291)
- Bel-duck: inkomende beltoon terwijl muziek loopt → stapsgewijs naar 32%.
- Herkent `USAGE_NOTIFICATION_RINGTONE`. Na de bel +2 terug, geen knal.
- Slaat over tijdens een gesprek en wacht als Alarm-duck nog herstelt.
- Chip Bel. Banner: Bel-duck actief (32%, beltoon hoorbaar).
- Zoeker: bel, bellen, ringtone, inkomend, gemist.
- Alleen Android 8+.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Timer-duck veldtest op TAH6519 met ANC aan
