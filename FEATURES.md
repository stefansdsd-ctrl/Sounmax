# Sounmax — volgende features

## Batch 294 (klaar — code)
1. NavDuck, PingDuck en AssistDuck zitten op DuckLane. Geen eigen `saved` meer.
2. Overlap met alarm/bel/timer bewaart niet het al verlaagde volume.
3. Caps: alarm 28, bel 32, timer 36, assistent 40, nav 42, ping 45. Laagste wint.
4. Uitzetten via chip laat de lane-owner los. Herstel pas als niemand vasthoudt.

## Batch 293 (klaar — code)
1. DuckLane: één baseline voor AlarmDuck, RingDuck en TimerDuck.
2. Overlappende ducks bewaren niet meer het al verlaagde volume.
3. Laagste cap wint. Herstel in stappen van +2 pas als alle owners loslaten.
4. Uitzetten via chip laat de lane ook los.

## Batch 292 (klaar — code)
1. TimerDuck: keukentimer of countdown-piep terwijl muziek loopt → stapsgewijs naar 36%.
2. Piep stopt → volume in stappen van +2 terug. Geen knal.
3. Niet tijdens gesprek, wekker of beltoon. Wacht als AlarmDuck of RingDuck actief is.
4. FeatureFinder-chip Timer. Zoek op timer, kookwekker, countdown, piep, keuken.
5. Banner: Timer-duck actief (36%, piep hoorbaar). Standaard aan. Poll elke 400 ms.
6. Alleen Android 8+. Herkent sonification + notification, niet ringtone/alarm.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Nav-duck veldtest: Maps-aanwijzing terwijl muziek + ANC loopt
