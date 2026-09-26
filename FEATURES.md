# Sounmax — volgende features

## Batch 184 (klaar — code)
1. QuietHours: nachtelijke volumecap (default 22–07, 40%).
2. BatterySaverDsp: bij ≤20% accu zware DSP overslaan.

## Batch 183 (klaar — code)
1. Complication-tik schakelt HearingGuard (dubbel = zoek, 3× = ANC).
2. Wear dosis-tegel toont cap + LIMIET + toggle-label.
3. Widget toont LIMIET als daglimiet vol is.

## Batch 182 (klaar — code)
1. Wear complication ranged HearingGuard (volume vs cap).
2. Widget-knop HearingGuard aan/uit.
3. Soft-cap toast bij overschrijding (max 1×/20s).
4. WearBridge publiceert guard + cap; CMD_TOGGLE_GUARD.

## Batch 181 (klaar — code)
1. Wear-tegel HearingGuard toggle + dose/cap.
2. Widget toont codec + volume vs cap.
3. GenreSceneHint in MediaSession-callback.
4. Hard-cap bij volume-wijziging.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest
