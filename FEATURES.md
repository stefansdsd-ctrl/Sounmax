# Sounmax — volgende features

## Batch 306 (klaar — code)
1. Pauze-hervat zit op DuckLane. Stilte 1,2–8s, daarna play → 68% voor 2s.
2. Alleen isMusicActive. Bel, alarm en nood blijven van zichzelf.
3. Chip uit = owner resume los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie.

## Batch 305 (klaar — code)
1. Wissel-duck zit op DuckLane. Muziek-app wisselt → 2,5s naar 62%.
2. Alleen media, game of unknown. Bel, alarm en nood blijven van zichzelf.
3. Chip uit = owner handoff los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie. Android 9+ (clientUid).

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Pauze-hervat veldtest: pauze 2s, daarna play
- Wissel-duck veldtest: Spotify naar YT Music
