## Batch 334 (nieuw)
- AlbumArt: iTunes Search API voor artworkUrl100 (opgeschaald 300). Alleen validated, niet metered/roaming/portal. Coil-ready URL.

## Batch 333 (nieuw)
- GenreEqSuggestion: EQ-preset voorstel op basis van nu-spelende titel (heuristiek).
- Chip of suggestie in UI later integreren.

## Batch 332 (nieuw)
- NoiseBasedAnc: suggestie volle/zachte ANC of transparantie op basis van WindNoiseDetector RMS + batterij/trend.
- Betere integratie software ANC hints.

## Batch 331 (nieuw)
- WindNoiseDetector: 3x short samples averaged for stability, threshold 0.10.
- BatteryHistory: more points (20), stricter slope filter (-0.05) for reliable ETA.

## Batch 330 (nieuw)
- WindNoiseDetector: echte korte AudioRecord RMS sample (200ms, permission check).
- BatteryHistory: ETA via lineaire regressie op recente samples, gecapt op 48u.
