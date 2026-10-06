# Sounmax — volgende features

## Batch 287 (klaar — code)
1. PingDuck: melding, alarm of beltoon terwijl muziek loopt → stapsgewijs naar 45%.
2. Toon stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Ping. Zoek op ping, melding, notificatie, alarm, beltoon.
4. Banner: Ping-duck actief (45%, melding hoorbaar). Standaard aan. Poll elke 800 ms.

## Batch 286 (klaar — code)
1. MicRestore: volume van vóór de mic-cap onthouden.
2. Opname stopt → stappen van +2 terug naar dat niveau, geen sprong.
3. FeatureFinder-chip Herstel. Zoek op herstel, terug, na mic.
4. Banner: Mic-herstel: volume komt terug na opname. Standaard aan.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Mic-cap veldtest: spraakbericht sturen terwijl muziek loopt
- Ping-duck veldtest: notificatie terwijl ANC + muziek loopt
