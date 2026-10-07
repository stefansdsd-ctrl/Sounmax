# Sounmax — volgende features

## Batch 291 (klaar — code)
1. RingDuck: inkomende beltoon terwijl muziek loopt → stapsgewijs naar 32%.
2. Bel stopt → volume in stappen van +2 terug. Geen knal.
3. Niet tijdens MODE_IN_CALL / MODE_IN_COMMUNICATION. Wacht als AlarmDuck actief is.
4. FeatureFinder-chip Bel. Zoek op bel, bellen, ringtone, inkomend, gemist.
5. Banner: Bel-duck actief (32%, beltoon hoorbaar). Standaard aan. Poll elke 450 ms.
6. Alleen Android 8+ (USAGE_NOTIFICATION_RINGTONE).

## Batch 290 (klaar — code)
1. AlarmDuck: wekker of alarm terwijl muziek loopt → stapsgewijs naar 28%.
2. Alarm stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Alarm. Zoek op alarm, wekker, klok, ringing.
4. Banner: Alarm-duck actief (28%, wekker hoorbaar). Standaard aan. Poll elke 500 ms.
5. Alleen Android 8+ (USAGE_ALARM). Oudere toestellen: duck blijft stil.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Bel-duck veldtest: inkomend gesprek terwijl muziek + ANC loopt
