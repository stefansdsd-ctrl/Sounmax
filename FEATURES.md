# Sounmax — volgende features

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

## Batch 291 (klaar — code)
1. RingDuck: inkomende beltoon terwijl muziek loopt → stapsgewijs naar 32%.
2. Bel stopt → volume in stappen van +2 terug. Geen knal.
3. Niet tijdens MODE_IN_CALL / MODE_IN_COMMUNICATION. Wacht als AlarmDuck actief is.
4. FeatureFinder-chip Bel. Zoek op bel, bellen, ringtone, inkomend, gemist.
5. Banner: Bel-duck actief (32%, beltoon hoorbaar). Standaard aan. Poll elke 450 ms.
6. Alleen Android 8+ (USAGE_NOTIFICATION_RINGTONE).

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Bel-duck veldtest: inkomend gesprek terwijl muziek + ANC loopt
- Timer-duck veldtest: keukentimer terwijl muziek + ANC loopt
