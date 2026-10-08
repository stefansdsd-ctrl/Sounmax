# Sounmax — volgende features

## Batch 308 (klaar — code)
1. Skip-duck zit op DuckLane. Stilte 0,25–1,1s, daarna play boven 58% → 1,5s naar 64%.
2. Vangt nummerwissel die pauze-hervat (1,2–8s) mist. Volume onder 58% doet niets.
3. Chip uit = owner skip los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie.

## Batch 307 (klaar — code)
1. Ochtend-cap zit op DuckLane. Eerste play van de dag boven 55% → 6s naar 48%.
2. Eén keer per kalenderdag. Volume onder 55% telt ook als "al gehad".
3. Chip uit = owner morning los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Skip-duck veldtest: skip tijdens luid nummer
