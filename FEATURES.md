# Sounmax — volgende features

## Batch 312 (klaar — code)
1. TunerNet: NET_CAPABILITY_VALIDATED + Wi-Fi vs mobiel.
2. GeminiAudioTuner slaat cloud over zonder gevalideerd internet.
3. Retry + fallback-modellen als het primaire model 404 of 5xx geeft.
4. Geen extra permissie.

## Batch 311 (klaar — code)
1. Gat-cap zit op DuckLane. Pauze 8–90s, play boven 60% → 7s naar 54%.
2. Vangt podcast/video die hervat-duck (max 8s) mist. Onder 60% doet niets.
3. Chip uit = owner gap los. Herstel gaat sneller als het gat naar de basis groot is.
4. Geen extra permissie.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
