# Sounmax — volgende features

## Batch 316 (klaar — code)
1. TunerNet.portal via NET_CAPABILITY_CAPTIVE_PORTAL.
2. TunerNet.slow: downstream 1–149 kbps. 0 = onbekend.
3. Portal slaat cloud altijd over. Traag net tot allowMetered.
4. AI-chip en knop tonen portal/traag. Zoeker: portal, captive, traag.
5. Geen extra permissie.

## Batch 314 (klaar — code)
1. TunerNet.metered via isActiveNetworkMetered (mobiel én metered hotspot).
2. Gemini slaat cloud over op metered tot allowMetered.
3. AI-scherm: waarschuwing + bevestigknop. Hoofdknop blijft lokaal.
4. askAiTuner geeft context door, zodat offline-check echt loopt.
5. Geen extra permissie.

## Batch 313 (klaar — code)
1. AI-balk: Wi-Fi, Mobiel, Ethernet of Offline.
2. Status volgt validated internet.
3. Offline-knop: lokale curve.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
