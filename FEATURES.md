# Sounmax — volgende features

## Batch 246 (klaar — code)
1. LowHsCap: HeadsetBatt ≤18% + STREAM_MUSIC >60% → cap 42%.
2. ReplugPlay: binnen 3 min na UnplugPause hervat KEYCODE_MEDIA_PLAY bij plug of ACL_CONNECTED.
3. WhyNow + FeatureFinder tonen de chips. Standaard aan.

## Batch 245 (klaar — code)
1. UnplugPause: ACTION_AUDIO_BECOMING_NOISY pauzeert media (niet alleen volume).
2. HeadsetBatt: Bluetooth BATTERY_LEVEL_CHANGED → chip met percentage.
3. WhyNow roept LockSoft.apply en SilentSoft.apply aan (batch 244 deed dat niet).

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
