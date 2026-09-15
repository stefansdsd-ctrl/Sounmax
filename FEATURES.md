# Sounmax — volgende features

## Batch 38 (software, deze commit)
- Auto-ANC: AmbientNoiseFloor → OFF / Ambient / Adaptive / Strong / Wind
- Vergeten-headset alert bij lang zwak RSSI (notificatie)
- Home-chips: Auto-ANC + Vergeten-alert

## Batch 37 (roadmap)
- Wear-tile: compacte chiprij (◀ scene ▶ + slaap + ANC) i.p.v. lange lijst
- RemoteViews-hoofdwidget + favorieten uitfaseren (alleen Glance)
- Find-headset: piep/flash via GATT of A2DP reconnect-hint
- Wekelijkse gehoor-samenvatting (uren + piek + weekcap)
- Lockscreen/QS: reconnect + laatste scene

## Batch 36 (software)
- Wear-chiprij: ◀ scene ▶ + slaaptimer
- Recent-scenes balk op home (laatste 5, zonder actieve)
- Scene-apply schrijft nu ook `RecentScenes`

## Nog te bouwen
- Wear-tile compacte chiprij afronden
- RemoteViews-widgets verwijderen na pin-check
- Echte GATT find-headset piep (nu A2DP-hint)

## Wacht op TAH6519 GATT-dump
- Echte multipoint write-payload
- Officiële ANC-karakteristiek + notify-leren bevestigen
