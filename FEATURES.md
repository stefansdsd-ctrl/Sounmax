# Sounmax — volgende features

## Batch 317 (klaar — code)
1. TunerNet.roaming: cellulair, validated, geen NOT_ROAMING. Wi-Fi niet.
2. Roaming slaat cloud over tot allowMetered. Chip en knop tonen roaming.
3. TunerNet.vpn: TRANSPORT_VPN. Alleen label, Gemini blijft aan.
4. Zoeker: roaming, vpn. Geen extra permissie.

## Batch 316 (klaar — code)
1. TunerNet.portal via NET_CAPABILITY_CAPTIVE_PORTAL.
2. TunerNet.slow: downstream 1–149 kbps. 0 = onbekend.
3. Portal slaat cloud altijd over. Traag net tot allowMetered.
4. AI-chip en knop tonen portal/traag. Zoeker: portal, captive, traag.
5. Geen extra permissie.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
