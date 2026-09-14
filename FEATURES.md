# Sounmax — volgende features

## Batch 37 (roadmap, deze commit)
- Wear-tile: compacte chiprij (◀ scene ▶ + slaap + ANC) i.p.v. lange lijst
- RemoteViews-hoofdwidget + favorieten uitfaseren (alleen Glance)
- Find-headset: piep/flash via GATT of A2DP reconnect-hint
- Wekelijkse gehoor-samenvatting (uren + piek + weekcap)
- Lockscreen/QS: reconnect + laatste scene

## Batch 36 (software)
- Wear-chiprij: ◀ scene ▶ + slaaptimer
- Recent-scenes balk op home (laatste 5, zonder actieve)
- Scene-apply schrijft nu ook `RecentScenes`

## Batch 35 (software)
- Glance-hoofdwidget (status + ANC / ◀▶ / DSP / undo / slaap / Nu)
- STIL + dual-battery op Glance
- refreshAll werkt Glance-hoofdwidget bij

## Batch 34 (software)
- Echte Glance-favorietenwidget (4 slots + “Nu”-suggestie)
- Suggestie-QS-tegel
- Scene-suggestie weegt uur-gebruik (`SceneUsage`) vóór weer/circadiaan

## Nog te bouwen
- Wear-tile compacte chiprij (batch 37)
- RemoteViews-widgets verwijderen na pin-check
- Find-headset + wekelijkse gehoor-samenvatting

## Wacht op TAH6519 GATT-dump
- Echte multipoint write-payload
- Officiële ANC-karakteristiek + notify-leren bevestigen
