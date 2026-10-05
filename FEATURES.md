# Sounmax — volgende features

## Batch 278 (klaar — code)
1. FocusQuietCap: NotificationManager interruption filter ≠ ALL + muziek actief, boven 70% → 55%, stapsgewijs.
2. Geen overlap met NightQuietCap (klok 22:30) of ChargeNightCap (oplader).
3. FeatureFinder-chip Focus. Zoek op niet storen, dnd, zen, concentratie.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Batch 276 (klaar — code)
1. PowerSaveCap: PowerManager.isPowerSaveMode + muziek actief, boven 72% → 60%, stapsgewijs.
2. Geen overlap met PhoneLowCap (≤15%) of BatterySaverDsp (alleen DSP-lagen).
3. FeatureFinder-chip Spaar. Zoek op spaarstand, powersave, batterijbesparing, zuinig.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
