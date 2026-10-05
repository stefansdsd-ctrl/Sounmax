# Sounmax — volgende features

## Batch 274 (klaar — code)
1. MeteredCap: mobiele data (niet roaming) + muziek actief, boven 70% → 58%, stapsgewijs.
2. Geen overlap met Roam-cap.
3. FeatureFinder-chip Data-cap. Zoek op data, mobiel, metered, stream, 4g, bundel.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Batch 273 (klaar — code)
1. PostCallRamp: na CALL_STATE_IDLE start volume op 40% en klimt in 8 stappen naar het niveau van vóór het gesprek.
2. CallModeGuard roept onEnter/onLeave aan.
3. FeatureFinder-chip Na-bel. Zoek op ophangen, na bel, opbouw, knal.
4. ActiveLimitBanner toont de opbouw.

## Batch 272 (klaar — code)
1. StillSafe: stilzitten (activity recognition, laatste 10 min) tussen 09:00 en 17:00 boven 82% → 72%, stapsgewijs. Geen overlap met Rij-cap, Avond-loop, Hardloop, Fiets of Doze.
2. ActivityTransitionReceiver past de bureau-cap meteen toe bij STILL.
3. FeatureFinder-chip Bureau. Zoek op stilzitten, bureau, desk, kantoor.
4. Cap opnieuw bij VOLUME_CHANGED en bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
