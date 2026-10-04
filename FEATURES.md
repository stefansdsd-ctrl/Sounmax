# Sounmax — volgende features

## Batch 269 (klaar — code)
1. BikeWind: fietsen (activity recognition, laatste 10 min) boven 72% → 55%, stapsgewijs. Wind op de helm. Geen overlap met Hardloop of Sport-app-cap.
2. ActivityTransitionReceiver past de fiets-cap meteen toe bij ON_BICYCLE.
3. FeatureFinder-chip Fiets. Zoek op fiets, helm, e-bike, cycling.
4. Bugfix: Wind-cap (hardlopen) werd niet opnieuw toegepast bij VOLUME_CHANGED.

## Batch 268 (klaar — code)
1. RunWind: hardlopen (activity recognition, laatste 8 min) boven 70% → 58%, stapsgewijs. Wind/impact. Geen overlap met Sport-app-cap.
2. ActivityTransitionReceiver slaat RUNNING apart op als run (was walk).
3. FeatureFinder-chip Hardloop. Zoek op rennen, wind, cardio, joggen.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
