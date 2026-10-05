# Sounmax — volgende features

## Batch 275 (klaar — code)
1. ThermalCap: telefoon thermal status matig of hoger + muziek actief, boven 68% → 55%, stapsgewijs.
2. Alleen API 29+. Geen overlap met Accu-DSP (die zet alleen zware lagen uit).
3. FeatureFinder-chip Warmte. Zoek op warmte, hitte, thermal, heet.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Batch 274 (klaar — code)
1. MeteredCap: mobiele data (niet roaming) + muziek actief, boven 70% → 58%, stapsgewijs.
2. Geen overlap met Roam-cap.
3. FeatureFinder-chip Data-cap. Zoek op data, mobiel, metered, stream, 4g, bundel.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
