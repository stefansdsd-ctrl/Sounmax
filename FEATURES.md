# Sounmax — volgende features

## Batch 245 (klaar — code)
1. UnplugPause: ACTION_AUDIO_BECOMING_NOISY pauzeert media (niet alleen volume).
2. HeadsetBatt: Bluetooth BATTERY_LEVEL_CHANGED → chip met percentage.
3. WhyNow roept LockSoft.apply en SilentSoft.apply aan (batch 244 deed dat niet).

## Batch 244 (klaar — code)
1. LockSoft: keyguard locked + STREAM_MUSIC >72% → cap 54%.
2. SilentSoft: RINGER_MODE_SILENT + STREAM_MUSIC >64% → cap 46%.
3. WhyNow + FeatureFinder tonen de chips. Standaard aan.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
