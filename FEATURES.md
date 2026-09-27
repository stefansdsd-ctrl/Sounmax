# Sounmax — volgende features

## Batch 190 (klaar — code)
1. Wear: media-rij ▶ / ⏮ / ⏭ + volume −/+.
2. Wear: zoek-telefoon + rusturen-cyclus + HS-zoek.
3. Wear-status: quiet_label + codec + RSSI + volume.
4. WearBridge: find_phone, cycle_quiet, next/prev_track.
5. MediaRemote.skip (volgende/vorige nummer).

## Batch 189 (klaar — code)
1. Wear: ▶ / − / + media-rij.
2. Wear: zoek-telefoon (FindPhoneHelper).
3. Wear: rusturen-cyclusknop + quiet_label/codec/RSSI/vol in status.
4. WearBridge: find_phone + cycle_quiet.

## Batch 188 (klaar — code)
1. Wear-app: rusturen-venster cyclus (uit → 22–7 → 23–8 → 21–6).
2. Wear-app: rusturen-cap cyclus 30/40/50/60%.
3. Statussync quiet_label naar horloge.

## Batch 187 (klaar — code)
1. Wear-tegel Accu: tik cyclus uit → 15 → 20 → 25 → 30%.
2. Wear-app knoppen AccuDSP + drempel.
3. Statussync saver + saver_thresh naar horloge.
4. WearBridge-commando's toggle/cycle/mode.

## Batch 186 (klaar — code)
1. Wear-tegel Accu-DSP (tik = aan/uit).
2. QS-tegel Accu-DSP.
3. Home-chip drempel cyclus 15/20/25/30%.
4. Wear-knop AccuDSP + status KEY_SAVER.

## Batch 185 (klaar — code)
1. Home-chip Accu-DSP (aan/uit + spaarstatus).
2. Home-chip nacht-cap (data.QuietHours, 22–07 40%).
3. Tool `saver` in Sport / Werk / Slaap.

## Batch 184 (klaar — code)
1. QuietHours: nachtelijke volumecap (default 22–07, 40%).
2. BatterySaverDsp: bij ≤20% accu zware DSP overslaan.

## Batch 183 (klaar — code)
1. Complication-tik schakelt HearingGuard (dubbel = zoek, 3× = ANC).
2. Wear dosis-tegel toont cap + LIMIET + toggle-label.
3. Widget toont LIMIET als daglimiet vol is.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest
