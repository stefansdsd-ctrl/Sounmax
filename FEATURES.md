# Sounmax — volgende features

## Batch 290 (klaar — code)
1. AlarmDuck: wekker of alarm terwijl muziek loopt → stapsgewijs naar 28%.
2. Alarm stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Alarm. Zoek op alarm, wekker, klok, ringing.
4. Banner: Alarm-duck actief (28%, wekker hoorbaar). Standaard aan. Poll elke 500 ms.
5. Alleen Android 8+ (USAGE_ALARM). Oudere toestellen: duck blijft stil.

## Batch 289 (klaar — code)
1. NavDuck: Maps of Waze spreekt terwijl muziek loopt → stapsgewijs naar 42%.
2. Aanwijzing stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip NavDuck. Zoek op navduck, maps, waze, aanwijzing, guidance.
4. Banner: Nav-duck actief (42%, aanwijzing hoorbaar). Standaard aan. Poll elke 650 ms.
5. AssistDuck en PingDuck worden nu gestart in MainActivity (ensure was nergens gekoppeld).
6. Alleen Android 8+ (active playback configs). Oudere toestellen: duck blijft stil.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Alarm-duck veldtest: wekker terwijl muziek + ANC loopt
