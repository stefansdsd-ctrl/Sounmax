# Sounmax — volgende features

## Batch 30 (software, deze commit)
- `ReconnectScene` — laatste scene-id klaarzetten bij BT-reconnect
- `SleepTimer` — 1–180 min, fade/stop via callback

## Nog te bouwen (waardevol, geen hardware-dump nodig)
1. Glance home-widget (Compose) i.p.v. RemoteViews
2. Wear: scene vorige/volgende + slaaptimer-chip
3. Widget-knoppen: ANC-cyclus, scene-undo, slaaptimer 15/30/60
4. Volume-fade laatste 60s van slaaptimer
5. Wekelijkse luistertijd-cap (gehoorbescherming)
6. Quick-settings tile: scene + headset-accu
7. Auto-LDAC/SBC op telefoonaccu <20% (deels aanwezig)

## Wacht op TAH6519 GATT-dump
- Echte multipoint write-payload
- Officiële ANC-karakteristiek + notify-leren bevestigen
