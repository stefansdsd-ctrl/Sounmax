# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-12c — batch 12 UI)
- Favorieten-rij boven zoek (max 4, long-press pin/unpin via FavoriteScenes).
- EQ A/B gebruikt EqAbCompare (live + haptic + 8s timeout).
- Commute-chip in SmartSuggestBar (06:30–09:30, gisteren pendelen).
- Scene-wissel: CommuteMemory + haptic per groep.
- One-ear fallback bij headset-refresh (soft-mono + crossfeed 60%).

## Nieuw (2026-09-12b — batch 12)
- Favoriete scenes: max 4 pins, long-press pin/unpin, rij boven zoek.
- EQ A/B compare: chip wisselt live, haptic, 8s timeout terug naar A.
- Commute-geheugen: eerste ochtendscene 06:30–09:30, suggestie volgende dag.
- Haptic scene-confirm per groep (werk / outdoor / slaap).
- QS-tile: volgende favoriet.
- One-ear fallback: L/R-delta >25% of één dop offline → soft-mono + crossfeed 60%.

## Nieuw (2026-09-12)
- Slaaptimer fade zet ANC uit na pauze.
- Dagelijks gehoorbudget-chip + soft-cap volume bij 100%.
- Pendelen + lopen/fietsen → walk/bike + OutdoorSafety.
- Crossfeed-chips 0 / 30 / 60%.
- Oplaadherinnering na 21:00 bij accu < 25%.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- Wear-complication: alleen favorieten (module nog koppelen)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
