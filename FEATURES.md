# Sounmax — volgende features

## Batch 216 (klaar — code)
1. FeatureFinderBar: zoekveld op home, tap cycle hold.
2. Laatste zoekquery onthouden.
3. Catalogus + apply-dispatcher (call/game/cinema/nap/…).

## Batch 215 (klaar — code)
1. ActiveLimitBanner: één zin waarom volume begrensd is (hold > gehoorcap).
2. FeatureFinder: zoek holds/scenes op trefwoord.
3. Statusbalk toont actieve limiet i.p.v. alleen laatste scene.
4. Wear CMD_LIMIT_STATUS.

## Batch 214 (klaar — code)
1. ThermalHold: cap volume + BatterySaverDsp bij ≥40°C (of geforceerd).
2. SceneHoldPriority: ziekenhuis > warmte > lage accu > bedtime.
3. BtReconnectRestore: scene+volume na BT-reconnect.
4. Wear CMD_CYCLE_THERMAL / CMD_TOGGLE_BT_RESTORE.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest
