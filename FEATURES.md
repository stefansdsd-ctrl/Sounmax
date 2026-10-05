# Sounmax — volgende features

## Batch 279 (klaar — code)
1. SilentRingerCap: ringerMode != NORMAL (tril/stil) + muziek actief, boven 68% → 52%, stapsgewijs.
2. Geen overlap met FocusQuietCap (interruption filter) of NightQuietCap (klok).
3. FeatureFinder-chip Stil. Zoek op tril, stil, silent, ringer, vibrate, beltoon.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Batch 278 (klaar — code)
1. FocusQuietCap: NotificationManager interruption filter ≠ ALL + muziek actief, boven 70% → 55%, stapsgewijs.
2. Geen overlap met NightQuietCap (klok 22:30) of ChargeNightCap (oplader).
3. FeatureFinder-chip Focus. Zoek op niet storen, dnd, zen, concentratie.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
