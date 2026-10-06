# Sounmax — volgende features

## Batch 286 (klaar — code)
1. MicRestore: volume van vóór de mic-cap onthouden.
2. Opname stopt → stappen van +2 terug naar dat niveau, geen sprong.
3. FeatureFinder-chip Herstel. Zoek op herstel, terug, na mic.
4. Banner: Mic-herstel: volume komt terug na opname. Standaard aan.

## Batch 285 (klaar — code)
1. MicLive: AudioRecordingCallback + activeRecordingConfigurations.
2. Muziek actief en opname bezig → volume stapsgewijs naar 40%.
3. FeatureFinder-chip Mic. Zoek op mic, microfoon, opname, recorder, spraakbericht.
4. Banner toont mic-cap. Refresh bij start en resume. Standaard aan.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Mic-cap veldtest: spraakbericht sturen terwijl muziek loopt
