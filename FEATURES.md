# Sounmax — volgende features

## Batch 289 (klaar — code)
1. NavDuck: Maps of Waze spreekt terwijl muziek loopt → stapsgewijs naar 42%.
2. Aanwijzing stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip NavDuck. Zoek op navduck, maps, waze, aanwijzing, guidance.
4. Banner: Nav-duck actief (42%, aanwijzing hoorbaar). Standaard aan. Poll elke 650 ms.
5. AssistDuck en PingDuck worden nu gestart in MainActivity (ensure was nergens gekoppeld).
6. Alleen Android 8+ (active playback configs). Oudere toestellen: duck blijft stil.

## Batch 288 (klaar — code)
1. AssistDuck: Gemini of Google Assistent praat terwijl muziek loopt → stapsgewijs naar 40%.
2. Stem stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Assist. Zoek op assistent, gemini, assistant, stem, ok google.
4. Banner: Assistent-duck actief (40%, stem hoorbaar). Standaard aan. Poll elke 700 ms.
5. Alleen Android 8+ (STREAM_ASSISTANT). Oudere toestellen: duck blijft stil.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Nav-duck veldtest: Maps-stem terwijl muziek + ANC loopt
