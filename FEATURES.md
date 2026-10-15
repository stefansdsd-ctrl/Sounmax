# Sounmax — volgende features

## Batch 322 (deels klaar)
1. Album art in dashboard chip (MediaNowPlaying.albumArt + Coil — al aanwezig).
2. EQ-slot picker in UI (0-4) + auto-apply bij connect bevestigen.
3. Low-battery duck/soft volume (optioneel, onder 15%) — LowBatteryDuck.kt toegevoegd.

## Batch 321 (klaar — code)
- EqPersist multi-slot (0-4) via JSON. save(slot), load(slot), clear.
- Auto-apply slot 0 voorbereid (ConnectEqApplier).

## Batch 320 (klaar — code)
- EqPersist.kt toegevoegd: save/load custom 10-band curves.

## Batch 319 (klaar — code)
1. HeadphoneBattery: reflection + HF-indicator (id=2) + BATTERY_LEVEL_CHANGED, chip + low notify (<20%).
2. MediaNowPlaying: title/artist/progress via actieve MediaSession (NotificationListener nodig), chip.
3. Auto-apply EqPersist bij Bluetooth connect (ConnectEqApplier + pending flag).

## Batch 318 (klaar — code)
1. TunerNet.congested: validated, geen NOT_CONGESTED. Portal en pauze niet.
2. Druk net slaat cloud over tot allowMetered. Chip en knop tonen druk.
3. TunerNet.suspended: internet, geen NOT_SUSPENDED. Cloud altijd uit.
4. Zoeker: druk, congested, pauze, suspended. Geen extra permissie.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
