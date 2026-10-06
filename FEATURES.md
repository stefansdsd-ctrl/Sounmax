# Sounmax — volgende features

## Batch 283 (klaar — code)
1. SpikeGuard: muziek actief, sprong > 3 stappen sinds vorige meting → vorige + 1.
2. Standaard aan. Geen sprong zonder muziek (alleen vorige stand onthouden).
3. FeatureFinder-chip Sprong. Zoek op sprong, volume, knal, piek, dempen.
4. Banner toont demping 8s. Refresh bij start en resume.

## Batch 281 (klaar — code)
1. OfflineGuard: ConnectivityManager + NET_CAPABILITY_VALIDATED. Geen aanname dat internet er is.
2. AI-tuner slaat cloud over als de guard aan staat en het netwerk niet gevalideerd is.
3. FeatureFinder-chip Net. Zoek op offline, net, internet, cloud, gemini, vliegtuig.
4. Banner toont offline-status. Refresh bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
