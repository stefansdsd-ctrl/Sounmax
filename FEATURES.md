# Sounmax — volgende features

## Batch 321 (klaar — code)
- EqPersist multi-slot (0-4) via JSON. save(slot), load(slot), clear.
- Auto-apply slot 0 voorbereid (ConnectEqApplier).

## Batch 320 (klaar — code)
- EqPersist.kt toegevoegd: save/load custom 10-band curves.

1. EqPersist: opslaan/laden custom 10-band curves via SharedPreferences. Call save/load from EQ UI of bij connect.

## Batch 319 (voorstel)
1. HeadphoneBattery: reflection getBatteryLevel + HF indicator fallback, chip + notificatie laag.
2. MediaNowPlaying: album art + progress via MediaSession, chip in dashboard.
3. Auto-apply EqPersist bij Bluetooth connect. (nu multi-slot)

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
