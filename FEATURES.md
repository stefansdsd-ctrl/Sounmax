# Sounmax — volgende features

## Batch 271 (klaar — code)
1. VehicleSafe: in de auto (activity recognition, laatste 15 min) zonder Android Auto boven 76% → 64%, stapsgewijs. Claxon en navigatie blijven hoorbaar. Geen overlap met DriveHold of CarCap.
2. ActivityTransitionReceiver past de rij-cap meteen toe bij IN_VEHICLE.
3. FeatureFinder-chip Rij-cap. Zoek op auto, rijden, claxon, navigatie.
4. Cap wordt opnieuw toegepast bij VOLUME_CHANGED en bij resume. Fiets- en avond-loop-cap ook bij resume.

## Batch 270 (klaar — code)
1. WalkSafe: lopen (activity recognition, laatste 12 min) tussen 20:00 en 06:00 boven 68% → 52%, stapsgewijs. Verkeer blijft hoorbaar. Geen overlap met Hardloop of Fiets.
2. ActivityTransitionReceiver past de avond-loop-cap meteen toe bij WALKING en ON_FOOT.
3. FeatureFinder-chip Avondloop. Zoek op lopen, avond, donker, verkeer.
4. Cap wordt ook opnieuw toegepast bij VOLUME_CHANGED.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
