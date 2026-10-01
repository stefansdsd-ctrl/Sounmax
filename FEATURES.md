# Sounmax — volgende features

## Batch 234 (klaar — code)
1. NoisyRoute: ACTION_AUDIO_BECOMING_NOISY + volume >32% → 24% (chip Route).
2. PeakCap: volume >90% → cap 78% (chip Piek).
3. WhyNow toont route-drop + piek-cap.

## Batch 233 (klaar — code)
1. SpeakerGuard: ACL-disconnect + volume >40% → 28% (chip Los).
2. AlarmSoon: wekker binnen 25 min + volume >55% → cap 40% (chip Wek).

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
