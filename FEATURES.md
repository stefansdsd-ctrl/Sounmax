# Sounmax — volgende features

## Batch 303 (klaar — code)
1. Spraak-duck zit op DuckLane. CONTENT_TYPE_SPEECH (audioboek, podcast, voice-note) → 43%.
2. Alleen media, unknown of game. Chat, ping, nav en bel blijven van zichzelf.
3. Chip uit = owner speech los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie. Android 8+.

## Batch 302 (klaar — code)
1. Chat-duck zit op DuckLane. Berichttoon (USAGE_NOTIFICATION / chat) → 44%.
2. Ping blijft 45% en dekt alleen korte event-pings. Chat wint van ping.
3. Chip uit = owner chat los. Herstel pas als niemand vasthoudt.
4. Geen extra permissie. Android 8+. Instant-chat usages vanaf Android 9.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- Spraak-duck veldtest: podcast over muziek
- Chat-duck veldtest: WhatsApp-toon terwijl muziek loopt
