# Sounmax — volgende features

## Batch 279 (klaar — code)
1. RingerQuietCap: beltoon stil of tril + muziek actief, boven 75% → 60%, stapsgewijs.
2. Geen overlap met FocusQuietCap (Niet storen / interruption filter).
3. FeatureFinder-chip Bel. Zoek op beltoon, tril, stil, silent, vibrate.
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
