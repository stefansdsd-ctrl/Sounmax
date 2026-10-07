# Sounmax — volgende features

## Batch 295 (klaar — code)
1. TtsDuck zit op DuckLane. Geen eigen `saved` meer.
2. TalkBack / STREAM_ACCESSIBILITY terwijl muziek loopt → stapsgewijs naar 34%.
3. Caps: alarm 28, bel 32, TTS 34, timer 36, assistent 40, nav 42, ping 45. Laagste wint.
4. Chip uit = owner los. Herstel pas als niemand vasthoudt. Alleen Android 8+.

## Batch 294 (klaar — code)
1. NavDuck, PingDuck en AssistDuck zitten op DuckLane. Geen eigen `saved` meer.
2. Overlap met alarm/bel/timer bewaart niet het al verlaagde volume.
3. Caps: alarm 28, bel 32, timer 36, assistent 40, nav 42, ping 45. Laagste wint.
4. Uitzetten via chip laat de lane-owner los. Herstel pas als niemand vasthoudt.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- TTS-duck veldtest: TalkBack terwijl muziek + ANC loopt
