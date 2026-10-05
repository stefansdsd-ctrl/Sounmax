# Sounmax — volgende features

## Batch 270 (klaar — code)
1. WalkSafe: lopen (activity recognition, laatste 12 min) tussen 20:00 en 06:00 boven 68% → 52%, stapsgewijs. Verkeer blijft hoorbaar. Geen overlap met Hardloop of Fiets.
2. ActivityTransitionReceiver past de avond-loop-cap meteen toe bij WALKING en ON_FOOT.
3. FeatureFinder-chip Avondloop. Zoek op lopen, avond, donker, verkeer.
4. Cap wordt ook opnieuw toegepast bij VOLUME_CHANGED.

## Batch 269 (klaar — code)
1. BikeWind: fietsen (activity recognition, laatste 10 min) boven 72% → 55%, stapsgewijs. Wind op de helm. Geen overlap met Hardloop of Sport-app-cap.
2. ActivityTransitionReceiver past de fiets-cap meteen toe bij ON_BICYCLE.
3. FeatureFinder-chip Fiets. Zoek op fiets, helm, e-bike, cycling.
4. Bugfix: Wind-cap (hardlopen) werd niet opnieuw toegepast bij VOLUME_CHANGED.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
