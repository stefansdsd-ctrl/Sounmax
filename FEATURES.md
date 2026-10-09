# Sounmax — volgende features

## Batch 318 (klaar — code)
1. TunerNet.congested: validated, geen NOT_CONGESTED. Portal en pauze niet.
2. Druk net slaat cloud over tot allowMetered. Chip en knop tonen druk.
3. TunerNet.suspended: internet, geen NOT_SUSPENDED. Cloud altijd uit.
4. Zoeker: druk, congested, pauze, suspended. Geen extra permissie.

## Batch 317 (klaar — code)
1. TunerNet.roaming: cellulair, validated, geen NOT_ROAMING. Wi-Fi niet.
2. Roaming slaat cloud over tot allowMetered. Chip en knop tonen roaming.
3. TunerNet.vpn: TRANSPORT_VPN. Alleen label, Gemini blijft aan.
4. Zoeker: roaming, vpn. Geen extra permissie.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
