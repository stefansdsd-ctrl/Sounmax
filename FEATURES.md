# Sounmax — volgende features

## Batch 276 (klaar — code)
1. PowerSaveCap: PowerManager.isPowerSaveMode + muziek actief, boven 72% → 60%, stapsgewijs.
2. Geen overlap met PhoneLowCap (≤15%) of BatterySaverDsp (alleen DSP-lagen).
3. FeatureFinder-chip Spaar. Zoek op spaarstand, powersave, batterijbesparing, zuinig.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Batch 275 (klaar — code)
1. ThermalCap: telefoon thermal status matig of hoger + muziek actief, boven 68% → 55%, stapsgewijs.
2. Alleen API 29+. Geen overlap met Accu-DSP (die zet alleen zware lagen uit).
3. FeatureFinder-chip Warmte. Zoek op warmte, hitte, thermal, heet.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
