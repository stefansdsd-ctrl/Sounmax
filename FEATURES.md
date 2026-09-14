# Sounmax — volgende features

## Batch 31 (software, deze commit)
- Wear-tile: vorige scene
- QS scene-tile toont headset-accu
- `SleepTimer` plant nu `SleepFade` (laatste 60s volume-fade)
- `ListenCap`: weekdosis ≥ WHO-limiet → volume −2 stappen (max 1×/uur)

## Nog te bouwen (waardevol, geen hardware-dump nodig)
1. Glance home-widget (Compose) i.p.v. RemoteViews
2. Widget-knoppen: ANC-cyclus, scene-undo, slaaptimer 15/30/60 (deels aanwezig)

## Wacht op TAH6519 GATT-dump
- Echte multipoint write-payload
- Officiële ANC-karakteristiek + notify-leren bevestigen
