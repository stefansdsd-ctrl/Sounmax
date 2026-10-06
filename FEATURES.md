# Sounmax — volgende features

## Batch 288 (klaar — code)
1. AssistDuck: Gemini of Google Assistent praat terwijl muziek loopt → stapsgewijs naar 40%.
2. Stem stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Assist. Zoek op assistent, gemini, assistant, stem, ok google.
4. Banner: Assistent-duck actief (40%, stem hoorbaar). Standaard aan. Poll elke 700 ms.
5. Alleen Android 8+ (STREAM_ASSISTANT). Oudere toestellen: duck blijft stil.

## Batch 287 (klaar — code)
1. PingDuck: melding, alarm of beltoon terwijl muziek loopt → stapsgewijs naar 45%.
2. Toon stopt → volume in stappen van +2 terug. Geen knal.
3. FeatureFinder-chip Ping. Zoek op ping, melding, notificatie, alarm, beltoon.
4. Banner: Ping-duck actief (45%, melding hoorbaar). Standaard aan. Poll elke 800 ms.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Assistent-duck veldtest: "Hey Google" terwijl muziek + ANC loopt
