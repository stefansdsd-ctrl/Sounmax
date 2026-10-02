# Sounmax — volgende features

## Batch 248 (klaar — code)
1. SessionCap: STREAM_MUSIC >62% gedurende 50 min → cap 52%. Timer reset als volume zakt.
2. CarCap: UiMode TYPE_CAR (Android Auto) + muziek >80% → cap 62%.
3. WhyNow + FeatureFinder tonen de chips. Standaard aan.

## Batch 247 (klaar — code)
1. AclPause: Bluetooth ACL_DISCONNECTED pauzeert media (UnplugPause mist veel BT-drops) en wist de accu-chip.
2. PhoneLowCap: telefoon ≤15% + STREAM_MUSIC >55% → cap 40%.
3. WhyNow + FeatureFinder tonen de chips. Standaard aan. Terug-play hervat ook na BT-pauze.

## Batch 246 (klaar — code)
1. LowHsCap: HeadsetBatt ≤18% + STREAM_MUSIC >60% → cap 42%.
2. ReplugPlay: binnen 3 min na UnplugPause hervat KEYCODE_MEDIA_PLAY bij plug of ACL_CONNECTED.
3. WhyNow + FeatureFinder tonen de chips. Standaard aan.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
