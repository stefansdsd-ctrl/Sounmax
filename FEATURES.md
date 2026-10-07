# Sounmax — volgende features

## Batch 296 (klaar — code)
1. MicLive zit op DuckLane. Geen eigen volume-stap meer, MicRestore bewaart geen `saved`.
2. Andere app neemt op terwijl muziek loopt → stapsgewijs naar 40%.
3. Caps: alarm 28, bel 32, TTS 34, timer 36, assistent 40, mic 40, nav 42, ping 45. Laagste wint.
4. Chip uit = owner los. Herstel pas als niemand vasthoudt. Callback start bij app-open. Alleen Android 7+.

## Batch 295 (klaar — code)
1. TtsDuck zit op DuckLane. Geen eigen `saved` meer.
2. TalkBack / STREAM_ACCESSIBILITY terwijl muziek loopt → stapsgewijs naar 34%.
3. Caps: alarm 28, bel 32, TTS 34, timer 36, assistent 40, nav 42, ping 45. Laagste wint.
4. Chip uit = owner los. Herstel pas als niemand vasthoudt. Alleen Android 8+.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
- TTS-duck veldtest: TalkBack terwijl muziek + ANC loopt
- Mic-cap veldtest: spraakbericht terwijl muziek loopt
