# Sounmax — volgende features

## Batch 156 (software)
- Punch: +1,6 dB op de laagste 2 bands
- Air: +1,5 dB op de hoogste band
- Loud: Fletcher-achtige laag+hoog boost
- Floor: schuif zodat min = 0 dB
- Ceil: schuif zodat max = 0 dB
- RMS: schaal naar RMS 2 dB

## Batch 155 (software)
- Mid: isoleer middelste 4 bands
- Snap: rond bands naar 0,5 dB
- V: V-curve (laag+hoog omhoog, mid omlaag)
- Scoop: middenboost, randen omlaag
- Abs: alle gains positief
- Presence: +1,4 dB op spraak/helderheid-bands

## Batch 154 (software)
- Clip6: bands knippen op ±6 dB
- Peak: schaal zodat max |gain| = 6 dB
- Dood: bands <0,35 dB naar 0
- Bass / Treble: isoleer lage of hoge 3 bands
- Jitter: ±0,4 dB random (met undo)

## Batch 153 (software)
- Invert: alle bands omdraaien van teken
- Halveer / Sterker: curve ×0,5 of ×1,25 (met undo)
- Schuif ←/→: bands één stap naar lager of hoger

## Batch 152 (software)
- Glad: 1-2-1 smoothing over de 10 bands (met undo)
- Spiegel: lage en hoge bands omdraaien
- Norm: gemiddelde naar 0 dB (volume-neutrale curve)
- Warm / Helder: tilt ±0,6 dB over de curve

## Batch 151 (software)
- Vlak-knop zet 10-bands + bass/virt/loud/clarity op 0 (eerst undo-punt)
- Kopieer EQ / Plak EQ via klembord (`SMX:gains|bass|virt|loud|clarity`)
- Closest-slot hint als de curve binnen 8 dB van Werk/Thuis/Sport ligt

## Batch 150 (software)
- Long-press op Werk/Thuis/Sport overschrijft een vol slot
- Laden van een slot maakt eerst een EQ-undo-punt
- Slots slaan tijdstempel op; wis via bestaande knop

## Batch 149 (software)
- EQ-redo: na undo één tik terug
- EQ vast: vergrendel bands tegen per ongeluk slepen
- Vaste slots Werk / Thuis / Sport (tik leeg = bewaren, tik vol = laden)

## Batch 148 (software)
- EQ-undo: max 5 punten, tik “EQ undo” om terug te gaan
- “Bewaar punt” slaat de huidige curve handmatig op
- Automatisch snapshot bij preset-wissel en band-sleep

## Batch 147 (software)
- Compacte catalogus: verberg ongebruikte scene-mappen (dagelijks optioneel)
- Meer → Compact aan / Verberg leeg / Toon alles
- Uursuggestie slaat verborgen mappen over en valt terug op favorieten

## Batch 146 (software)
- Volume-cap Quick Settings-tegel (past HearingDoseGuard toe)
- Wear-tegel dosis: minuten + waarschuwing, tik = volume-cap
- Wear-app: Cap-knop + Pauze (oorpauze forceren)
- Status naar Wear: dose_warn + cap_pct

## Nog te bouwen
- Find-beep hardware-payload finetunen na echte TAH6519 GATT-dump
- ANC-GATT dump TAH6519 + notify-leren
- Scene-suggestie fine-tunen na veldtest
